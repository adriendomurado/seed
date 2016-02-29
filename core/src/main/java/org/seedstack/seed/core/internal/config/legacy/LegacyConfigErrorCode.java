/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.seed.core.internal.config.legacy;

import org.seedstack.seed.ErrorCode;

enum LegacyConfigErrorCode implements ErrorCode {
    UNABLE_TO_LOAD_CONFIGURATION_RESOURCE,
    UNABLE_TO_INSTANTIATE_CONFIGURATION_ARRAY,
    CONFIGURATION_ERROR,
    CONVERTER_NOT_COMPATIBLE,
    CONVERTER_INSTANTIATION,
    CONVERTER_CONSTRUCTOR_ILLEGAL_ACCESS,
    NO_SUITABLE_CONFIGURATION_LOOKUP_CONSTRUCTOR_FOUND,
    UNABLE_TO_INSTANTIATE_CONFIGURATION_LOOKUP,
    FIELD_ILLEGAL_ACCESS
}
