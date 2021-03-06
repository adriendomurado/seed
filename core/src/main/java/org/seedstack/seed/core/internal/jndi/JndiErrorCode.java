/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.seed.core.internal.jndi;

import org.seedstack.shed.exception.ErrorCode;

/**
 * JNDI error codes.
 *
 * @author adrien.lauer@mpsa.com
 */
enum JndiErrorCode implements ErrorCode {
    MISSING_JNDI_PROPERTIES,
    UNABLE_TO_CONFIGURE_ADDITIONAL_JNDI_CONTEXT,
    UNABLE_TO_CONFIGURE_DEFAULT_JNDI_CONTEXT,
    UNABLE_TO_REGISTER_INJECTION_FOR_RESOURCE
}
