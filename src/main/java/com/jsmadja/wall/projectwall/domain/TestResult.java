/**
 * Copyright (C) 2010 Julien SMADJA <julien.smadja@gmail.com> - Arnaud LEMAIRE
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jsmadja.wall.projectwall.domain;


public final class TestResult {

    private int failCount;
    private int passCount;
    private int skipCount;
    private int totalCount;

    public final int getFailCount() {
        return failCount;
    }
    public final void setFailCount(int failCount) {
        this.failCount = failCount;
    }
    public final int getPassCount() {
        return passCount;
    }
    public final void setPassCount(int passCount) {
        this.passCount = passCount;
    }
    public final int getSkipCount() {
        return skipCount;
    }
    public final void setSkipCount(int skipCount) {
        this.skipCount = skipCount;
    }
    public final int getTotalCount() {
        return totalCount;
    }
    public final void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
