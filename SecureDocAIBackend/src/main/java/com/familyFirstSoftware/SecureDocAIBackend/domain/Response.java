package com.familyFirstSoftware.SecureDocAIBackend.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import org.springframework.http.HttpStatus;

import java.util.Map;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT;

/**
 * @author Lee Scott
 * @version 1.0
 * @license FamilyFirstSoftware, LLC (<a href="https://www.FamilyFirstSoftware.com"> FFS, LLC</a>)
 * @email FamilyFirstSoftware@gmail.com
 * @since 12/14/2024
 *
 * Format of the response either to the client or to the React app.
 */

@JsonInclude(NON_DEFAULT)
@Builder
public record Response(String time, int code, String path, HttpStatus status, String message, String exception, Map<?, ?> data) {

}

