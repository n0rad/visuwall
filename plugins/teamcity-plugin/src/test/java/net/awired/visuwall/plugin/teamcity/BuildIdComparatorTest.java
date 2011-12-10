package net.awired.visuwall.plugin.teamcity;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BuildIdComparatorTest {

    BuildIdComparator comparator = new BuildIdComparator();

    @Test
    public void should_compare_asc() {
        assertEquals(-1, comparator.compare("1", "2"));
    }

    @Test
    public void should_compare_desc_with_two_chars() {
        assertEquals(1, comparator.compare("13", "6"));
    }

    @Test
    public void should_compare_equal() {
        assertEquals(0, comparator.compare("1", "1"));
    }

    @Test
    public void should_compare_numeric_with_text() {
        assertEquals(0, comparator.compare("1", "text"));
    }
}
