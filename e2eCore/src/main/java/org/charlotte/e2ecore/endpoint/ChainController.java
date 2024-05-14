package org.charlotte.e2ecore.endpoint;

import org.charlotte.e2ecore.exception.IatmException;
import org.charlotte.e2ecore.service.*;
import org.charlotte.e2ecore.utils.TokenUtils;
import org.charlotte.e2edomain.Case;
import org.charlotte.e2edomain.Chain;
import org.charlotte.e2edomain.ChainData;
import org.charlotte.e2edomain.dto.ChainBatchQueryDTO;
import org.charlotte.e2edomain.dto.ChainCreateDTO;
import org.charlotte.e2edomain.dto.ChainExecDTO;
import org.charlotte.e2edomain.dto.ChainUpdateDTO;
import org.charlotte.e2edomain.enums.ChainType;
import org.charlotte.e2edomain.response.ChainExecResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


@Slf4j
@RestController
@RequestMapping("/api/chain")
public class ChainController {
    @Resource
    private CollectionService collectionService;

    @Resource
    private CaseService caseService;

    @Resource
    private ChainService chainService;

    @Resource
    private ChainDataService chainDataService;

    @Resource
    private ExecuteService executeService;


    @PostMapping("/create")
//    @PreAuthorize("hasAnyAuthority('CHAIN_ADD')")
    public Chain create(@RequestBody ChainCreateDTO dto) {
        Map<String, Object> userClaim = TokenUtils.getUserClaim();
        String email = (String) userClaim.get("email");

        String chainType = dto.getChainType();
        if (!(ChainType.CASE_CHAIN.name().equals(chainType) || ChainType.DATA_TOOL.name().equals(chainType))) {
            // 如果 前端传递的 chainType 不是 dataTool or caseChain, 那么 设置默认值为： caseChain
            chainType = String.valueOf(ChainType.CASE_CHAIN);
        }

        Chain chain = Chain.builder()
                .name(dto.getChainName())
                .caseList(caseService.queryCasesByPrimaryKeys(dto.getCaseIdList()))
                .createDateTime(LocalDateTime.now())
                .isDeleted(Boolean.FALSE)
                .type(chainType)
                .creator(email)
                .build();
        return collectionService.saveOrUpdate(chain);
    }

    @PostMapping("/update")
//    @PreAuthorize("hasAnyAuthority('CHAIN_UPDATE')")
    public Chain update(@RequestBody ChainUpdateDTO dto) throws IatmException {
        Chain chain = collectionService.getDocumentById(dto.getId(), Chain.class);

        Pair<List<Case>, List<String>> listPair = caseService.queryCasesWithInvalidByPrimaryKeys(dto.getCaseIdList());
        if (CollectionUtils.isNotEmpty(listPair.getRight())) {
            throw new IatmException("no case(s) found:" + listPair.getRight());
        }
        chain.setCaseList(caseService.queryCasesByPrimaryKeys(dto.getCaseIdList()));

        chain.setName(dto.getChainName());
        chain.setType(dto.getChainType());
        chain.setUpdateDateTime(LocalDateTime.now());
        return collectionService.saveOrUpdate(chain);
    }

    @PostMapping("batchQuery")
    public List<Chain> batchQuery(@RequestBody ChainBatchQueryDTO dto) {
        return chainService.queryChainByConditions(dto);
    }

    @GetMapping("query")
    public Chain getSingleChainInfo(@RequestParam(value = "id") String chainId) throws IatmException {
        return collectionService.getDocumentById(chainId, Chain.class);
    }

    @DeleteMapping("delete")
    @PreAuthorize("hasAnyAuthority('CHAIN_DEL')")
    public void deleteChain(@RequestParam(value = "id") String chainId) throws Exception {
        // todo: 删除chain的时候 需要把对应的chain_data数据也一并删除了, 此处需要事务处理！！！
        Chain chain = collectionService.getDocumentById(chainId, Chain.class);
        if (chain.getIsDeleted()) {
            return;
        }
        chainService.deleteChain(chainId);
    }

    @GetMapping("getAllUndeletedChainIdList")
    public void getAllUndeletedChainIdList() {
        chainService.getAllUndeletedChainList();
    }


    @PostMapping("exec/start")
//    @PreAuthorize("hasAnyAuthority('CHAIN_RUN')")
    public ChainExecResponse startChainExec(@RequestBody ChainExecDTO chainExecDTO) {
        log.info("========> start chain: " + chainExecDTO);

        ChainData chainExecutionContext = chainDataService.createChainData(
                chainExecDTO.getChainId(),
                chainExecDTO.getEnv(),
                chainExecDTO.getInitParams(),
                chainExecDTO.getJiraId()
        );
        executeService.doExecute(chainExecutionContext);
        return ChainExecResponse.builder()
                .executionId(chainExecutionContext.getId().toHexString())
                .build();
    }
}
