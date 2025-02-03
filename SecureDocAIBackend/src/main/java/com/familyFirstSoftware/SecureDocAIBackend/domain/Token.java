package com.familyFirstSoftware.SecureDocAIBackend.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Lee Scott
 * @version 1.0
 * @license FamilyFirstSoftware, LLC (<a href="https://www.FamilyFirstSoftware.com"> FFS, LLC</a>)
 * @email FamilyFirstSoftware@gmail.com
 * @since 2/3/2025
 */

@Builder
@Getter
@Setter
public class Token {
    private String access;
    private String refresh;
}

