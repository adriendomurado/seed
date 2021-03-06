/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.seed;

import org.seedstack.coffig.Config;

import javax.validation.constraints.NotNull;

@Config("data")
public class DataConfig {
    public enum ImportMode {
        AUTO,
        FORCE,
        DISABLED
    }

    @NotNull
    private ImportMode importMode = ImportMode.AUTO;

    public ImportMode getImportMode() {
        return importMode;
    }

    public void setImportMode(ImportMode importMode) {
        this.importMode = importMode;
    }
}
