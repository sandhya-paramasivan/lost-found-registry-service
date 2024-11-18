package com.api.enums;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StatusEnum {
    NEW,
    TO_BE_CLAIMED,
    CLAIMED
}
