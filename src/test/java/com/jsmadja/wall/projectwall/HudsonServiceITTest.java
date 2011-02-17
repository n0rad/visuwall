package com.jsmadja.wall.projectwall;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

public class HudsonServiceITTest {

    private static HudsonService hudsonService = new HudsonService(Integration.HUDSON_URL);
    private static List<HudsonJob> jobs;

    @BeforeClass
    public static void init() {
        jobs = hudsonService.findAllJobs();
    }

    @Test
    public void should_retrieve_jobs_from_hudson() {
        assertFalse(jobs.isEmpty());
    }

    @Test
    public void should_retrieve_jobs_with_status() {
        for (HudsonJob job:jobs) {
            System.err.println(job);
            assertNotNull(job.isSuccessful());
        }
    }

    @Test
    public void should_retrieve_jobs_with_description() {
        for (HudsonJob job:jobs) {
            System.err.println(job);
            assertNotNull(job.getDescription());
        }
    }

    @Test
    public void should_retrieve_jobs_with_building_status() {
        for (HudsonJob job:jobs) {
            System.err.println(job);
            assertNotNull(job.isBuilding());
        }
    }

    @Test
    public void should_retrieve_job_with_last_commiters() {
        HudsonJob job = hudsonService.findJob("synthesis", 273);
        assertNotNull(job.getCommiters());
    }

    @Test
    public void should_retrieve_average_build_duration_time() {
        long duration = hudsonService.getAverageBuildDurationTime("synthesis");
        assertTrue(duration > 0);
    }

    @Test
    public void should_retrieve_build_start_time() {
        HudsonJob job = hudsonService.findJob("synthesis", 273);
        assertNotNull(job.getStartTime());
    }

    @Test
    public void should_retrieve_estimated_remaining_time() {
        long estimatedRemainingTime = hudsonService.getEstimatedRemainingTime("tester-api-staging");
        assertTrue(estimatedRemainingTime > 0);
    }

    @Test
    public void estimated_remaining_time_shoud_be_equal_to_0_for_a_finished_job() {
        long estimatedRemainingTime = hudsonService.getEstimatedRemainingTime("parent");
        assertEquals(0, estimatedRemainingTime);
    }

    @Test
    public void should_retrieve_artifact_id() {
        String artifactId = hudsonService.findJob("synthesis").getArtifactId();
        assertEquals("com.orangevallee.on.server.synthesis:synthesis", artifactId);
    }
}
