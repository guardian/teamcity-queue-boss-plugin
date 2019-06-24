package com.gu.teamcity.plugins;

import jetbrains.buildServer.serverSide.Branch;
import jetbrains.buildServer.serverSide.BuildPromotionEx;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import org.junit.Test;

public class ServerListenerTest {


    @Test
    public void testMasterBranchFirstComparator() {

        final BuildPromotionEx zero = mock(BuildPromotionEx.class);
        final BuildPromotionEx one = mock(BuildPromotionEx.class);
        final Branch branchOne = mock(Branch.class);
        final BuildPromotionEx two = mock(BuildPromotionEx.class);
        final Branch branchTwo = mock(Branch.class);
        final BuildPromotionEx three = mock(BuildPromotionEx.class);
        final Branch branchThree = mock(Branch.class);
        final BuildPromotionEx four = mock(BuildPromotionEx.class);
        final Branch branchFour = mock(Branch.class);

        when(zero.getBranch()).thenReturn(null);
        when(one.getBranch()).thenReturn(branchOne);
        when(branchOne.getName()).thenReturn("some_dev_branch");
        when(two.getBranch()).thenReturn(branchTwo);
        when(branchTwo.getName()).thenReturn("master");
        when(three.getBranch()).thenReturn(branchThree);
        when(branchThree.getName()).thenReturn("other_dev_branch");
        when(four.getBranch()).thenReturn(branchFour);
        when(branchFour.getName()).thenReturn(Branch.DEFAULT_BRANCH_NAME);

        List<BuildPromotionEx> input = Arrays.asList(zero, one, two, three, four);

        input.sort(ServerListener.MASTER_BRANCH_FIRST_COMPARATOR);

        String[] expected = {Branch.DEFAULT_BRANCH_NAME, "master", "some_dev_branch", "other_dev_branch", null};

        assertArrayEquals(
                expected,
                input.stream().map(build -> build.getBranch()==null ? null : build.getBranch().getName()).toArray(String[]::new)
        );

    }

    @Test
    public void testFairProjectDistributionComparator() {
        final BuildPromotionEx zero = mock(BuildPromotionEx.class);
        when(zero.getProjectId()).thenReturn("flood_subA");
        final BuildPromotionEx one = mock(BuildPromotionEx.class);
        when(one.getProjectId()).thenReturn("flood_subB");
        final BuildPromotionEx two = mock(BuildPromotionEx.class);
        when(two.getProjectId()).thenReturn("oneoff");
        final BuildPromotionEx three = mock(BuildPromotionEx.class);
        when(three.getProjectId()).thenReturn("twoofthisone");
        final BuildPromotionEx four = mock(BuildPromotionEx.class);
        when(four.getProjectId()).thenReturn("flood_subC");
        final BuildPromotionEx five = mock(BuildPromotionEx.class);
        when(five.getProjectId()).thenReturn("twoofthisone");
        final BuildPromotionEx six = mock(BuildPromotionEx.class);
        when(six.getProjectId()).thenReturn("flood_subA");
        final BuildPromotionEx seven = mock(BuildPromotionEx.class);
        when(seven.getProjectId()).thenReturn("threeofthisone");
        final BuildPromotionEx eight = mock(BuildPromotionEx.class);
        when(eight.getProjectId()).thenReturn("flood_subB");
        final BuildPromotionEx nine = mock(BuildPromotionEx.class);
        when(nine.getProjectId()).thenReturn("threeofthisone");
        final BuildPromotionEx ten = mock(BuildPromotionEx.class);
        when(ten.getProjectId()).thenReturn("flood_subC");
        final BuildPromotionEx eleven = mock(BuildPromotionEx.class);
        when(eleven.getProjectId()).thenReturn("threeofthisone");
        final BuildPromotionEx twelve = mock(BuildPromotionEx.class);
        when(twelve.getProjectId()).thenReturn("flood_subA");
        final BuildPromotionEx thirteen = mock(BuildPromotionEx.class);
        when(thirteen.getProjectId()).thenReturn("flood_subB");
        final BuildPromotionEx fourteen = mock(BuildPromotionEx.class);
        when(fourteen.getProjectId()).thenReturn("flood_subC");

        List<BuildPromotionEx> input = Arrays.asList(
                zero, one, two, three, four, five, six, seven, eight, nine, ten, eleven, twelve, thirteen, fourteen
        );

        // mutation...
        input.sort(ServerListener.getFairProjectDistributionComparator(
                ServerListener.groupProjectsByOriginProjectIdPart(input.stream())
        ));

        String[] expected = {
                "flood_subA", "oneoff", "twoofthisone", "threeofthisone", "flood_subB", "twoofthisone", "threeofthisone",
                "flood_subC", "threeofthisone", "flood_subA", "flood_subB", "flood_subC", "flood_subA", "flood_subB", "flood_subC"
        };

        assertArrayEquals(
                expected,
                input.stream().map(BuildPromotionEx::getProjectId).toArray(String[]::new)
        );

    }



}