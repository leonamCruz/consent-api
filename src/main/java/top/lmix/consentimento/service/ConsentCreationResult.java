package top.lmix.consentimento.service;

import top.lmix.consentimento.domain.entity.ConsentEntity;

public record ConsentCreationResult(ConsentEntity consent, boolean created) {
}
