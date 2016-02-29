/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.seed.core.internal.application;

import org.seedstack.seed.ErrorCode;

/**
 * SEED application error codes.
 *
 * @author adrien.lauer@mpsa.com
 */
public enum ApplicationErrorCode implements ErrorCode {
    MISSING_APPLICATION_IDENTIFIER,
    STORAGE_PATH_IS_NOT_A_DIRECTORY,
    UNABLE_TO_CREATE_STORAGE_DIRECTORY,
    STORAGE_DIRECTORY_IS_NOT_WRITABLE,

    UNABLE_TO_GENERATE_INJECTION_GRAPH,

    NO_LOCAL_STORAGE_CONFIGURED
}
