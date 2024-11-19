package com.api.enums;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StatusEnum {
    LOST,
    TO_BE_CLAIMED,
    CLAIMED
}
