package com.example.Automated.Application.Mangament.serviceImplements;

import com.example.Automated.Application.Mangament.dto.request.RuleCriteria;
import com.example.Automated.Application.Mangament.model.DocumentRuleValue;
import com.example.Automated.Application.Mangament.model.InputDocumentMatrix;
import com.example.Automated.Application.Mangament.model.Position;
import com.example.Automated.Application.Mangament.repositories.InputDocumentMatrixRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PositionRuleService {

    private final InputDocumentMatrixRepository matrixRepository;

    public PositionRuleService(InputDocumentMatrixRepository matrixRepository) {
        this.matrixRepository = matrixRepository;
    }

    @Transactional(readOnly = true)
    public List<RuleCriteria> getRulesByPositionAndDocument(long positionId, long documentId) {

        System.out.println(">>> ĐANG CHẠY VÀO HÀM VỚI ID: " + positionId + ", " + documentId);

        try {
            Optional<InputDocumentMatrix> matrixOpt = matrixRepository.findByPositionIdAndDocumentId(positionId, documentId);

            if (matrixOpt.isEmpty()) {
                System.out.println(">>> CẢNH BÁO: Không tìm thấy Matrix trong DB!");
                return new ArrayList<>();
            }


            List<DocumentRuleValue> ruleValues = matrixOpt.get().getDocumentRuleValueList();

            if (ruleValues == null || ruleValues.isEmpty()) {
                System.out.println(">>> List Rule rỗng, trả về ArrayList mới.");
                return new ArrayList<>();
            }


            return ruleValues.stream()
                    .map(this::convertToRuleCriteria)
                    .collect(Collectors.toList());

        } catch (Throwable t) {
            System.err.println("!!! LỖI CỰC NGHIÊM TRỌNG: ");
            t.printStackTrace();
            throw new RuntimeException(t);
        }
    }

    @Transactional(readOnly = true)
    public List<RuleCriteria> getRequiredRulesForPosition(Position position) {
        List<RuleCriteria> requiredRules = new ArrayList<>();
        List<InputDocumentMatrix> matrices = matrixRepository.findByPosition(position);

        for (InputDocumentMatrix matrix : matrices) {
            List<DocumentRuleValue> ruleValues = matrix.getDocumentRuleValueList();
            if (ruleValues != null) {
                ruleValues.stream()
                        .map(this::convertToRuleCriteria)
                        .forEach(requiredRules::add);
            }
        }
        return requiredRules;
    }


    private RuleCriteria convertToRuleCriteria(DocumentRuleValue entity) {
        String ruleName = "Unknown Rule";
        String ruleValue = entity.getRuleValue();

        if (entity.getDocumentRule() != null) {
            ruleName = entity.getDocumentRule().getDocumentRuleName();
        }


        return new RuleCriteria(
                ruleName,
                ruleValue,
                ruleName
        );
    }
}