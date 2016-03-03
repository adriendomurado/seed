/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.seed.cli;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.seedstack.seed.it.AbstractSeedIT;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

public class WithWithCommandLineIT extends AbstractSeedIT {
    private static boolean passedBeforeClass = false;
    private static boolean passedBefore = false;
    private static boolean passedAfter = false;
    private static boolean passedAfterClass = false;

    @Inject
    private Fixture fixture;

    @BeforeClass
    public static void beforeClass() {
        assertThat(passedBeforeClass).isFalse();
        assertThat(passedBefore).isFalse();
        assertThat(DummyCommandLineHandler.called).isFalse();
        assertThat(passedAfter).isFalse();
        assertThat(passedAfterClass).isFalse();
        passedBeforeClass = true;
    }

    @Before
    public void before() {
        assertThat(passedBeforeClass).isTrue();
        assertThat(passedBefore).isFalse();
        assertThat(DummyCommandLineHandler.called).isFalse();
        assertThat(passedAfter).isFalse();
        assertThat(passedAfterClass).isFalse();
        passedBefore = true;
    }

    @After
    public void after() {
        assertThat(passedBeforeClass).isTrue();
        assertThat(passedBefore).isTrue();
        assertThat(DummyCommandLineHandler.called).isTrue();
        assertThat(passedAfter).isFalse();
        assertThat(passedAfterClass).isFalse();
        passedAfter = true;
    }

    @AfterClass
    public static void afterClass() {
        assertThat(passedBeforeClass).isTrue();
        assertThat(passedBefore).isTrue();
        assertThat(DummyCommandLineHandler.called).isTrue();
        assertThat(passedAfter).isTrue();
        assertThat(passedAfterClass).isFalse();
        passedAfterClass = true;
    }

    @Test
    @WithCommandLine(args = { "arg0", "arg1", "--option=value" }, expectedExitCode = 255, command = "dummy")
    public void test_with_annotation() {
        assertThat(passedBeforeClass).isTrue();
        assertThat(passedBefore).isTrue();
        assertThat(DummyCommandLineHandler.called).isTrue();
        assertThat(fixture).isNotNull();
    }
}
