/**
 *     Copyright (C) 2010 Julien SMADJA <julien dot smadja at gmail dot com> - Arnaud LEMAIRE <alemaire at norad dot fr>
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *             http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

package fr.norad.visuwall.core.business.service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import fr.norad.visuwall.api.domain.SoftwareProjectId;
import fr.norad.visuwall.api.plugin.capability.BuildCapability;
import fr.norad.visuwall.api.plugin.capability.ViewCapability;
import fr.norad.visuwall.core.exception.MissingCapacityException;
import fr.norad.visuwall.core.persistence.entity.SoftwareAccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SoftwareAccessService {

    private static final Logger LOG = LoggerFactory.getLogger(SoftwareAccessService.class);

    public Set<SoftwareProjectId> discoverBuildProjects(SoftwareAccess softwareAccess)
            throws MissingCapacityException {
        if (!(softwareAccess.getConnection() instanceof BuildCapability)) {
            throw new MissingCapacityException(
                    "Can not found build projects in a software who does not implement BuildCapability "
                            + softwareAccess);
        }
        Set<SoftwareProjectId> res = new HashSet<SoftwareProjectId>();
        BuildCapability buildPlugin = (BuildCapability) softwareAccess.getConnection();
        if (softwareAccess.isAllProject()) {
            Map<SoftwareProjectId, String> projectIds = buildPlugin.listSoftwareProjectIds();
            res.addAll(projectIds.keySet());
        } else {
            List<SoftwareProjectId> nameProjectIds = softwareAccess
                    .getProjectIds();
            if (nameProjectIds == null) {
                LOG.warn("plugin return null on findSoftwareProjectIdsByNames", buildPlugin);
            } else {
                res.addAll(nameProjectIds);
            }

            if (buildPlugin instanceof ViewCapability) {
                List<SoftwareProjectId> viewProjectIds = ((ViewCapability) buildPlugin)
                        .findSoftwareProjectIdsByViews(softwareAccess.getViewNames());
                if (viewProjectIds == null) {
                    LOG.warn("plugin return null on findSoftwareProjectIdsByViews", buildPlugin);
                } else {
                    res.addAll(viewProjectIds);
                }
            }
        }
        return res;
    }
}
