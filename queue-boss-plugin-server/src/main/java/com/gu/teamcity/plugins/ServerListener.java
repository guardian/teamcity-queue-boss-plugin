package com.gu.teamcity.plugins;

import jetbrains.buildServer.serverSide.*;
import jetbrains.buildServer.util.EventDispatcher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ServerListener extends BuildServerAdapter {

    private final BuildQueue myQueue;
    private final BuildQueueEx myQueueEx;

    public ServerListener(@NotNull final EventDispatcher<BuildServerListener> dispatcher,
                          @NotNull final BuildQueue queue,
                          @NotNull final BuildQueueEx queueEx
    ) {
        myQueue = queue;
        myQueueEx = queueEx;
        dispatcher.addListener(this);
    }

    @Override
    public void buildTypeAddedToQueue(@NotNull SQueuedBuild queuedBuild) {
        super.buildTypeAddedToQueue(queuedBuild);

        @NotNull List<QueuedBuildEx> items = myQueueEx.getItemsEx();

        Map<String, List<BuildPromotionEx>> groupedByProjectId =
                items.stream().map(QueuedBuildEx::getBuildPromotion).collect(
                        Collectors.groupingBy(BuildPromotionEx::getProjectId)
                );

        myQueue.applyOrder(
                items.stream()
                        .map(QueuedBuildEx::getBuildPromotion)
                        .sorted(MASTER_BRANCH_FIRST_COMPARATOR
                                .thenComparing(
                                        getFairProjectDistributionComparator(groupedByProjectId)
                                )
                                .thenComparing(BUILD_ID_COMPARATOR)
                        )
                        .map(BuildPromotion::getId)
                        .map(Object::toString)
                        .toArray(String[]::new)
        );

    }

    private static boolean branchIsMaster(Branch branch) {
        return branch.getName().equals(Branch.DEFAULT_BRANCH_NAME) || branch.getName().toLowerCase().trim().equals("master");
    }

    public static final Comparator<BuildPromotionEx> MASTER_BRANCH_FIRST_COMPARATOR = (buildOne, buildTwo) -> {

        @Nullable Branch buildOneBranch = buildOne.getBranch();
        @Nullable Branch buildTwoBranch = buildTwo.getBranch();

        if(buildOneBranch == null && buildTwoBranch == null) return 0;

        if(buildOneBranch == null || buildOneBranch.getName().trim().isEmpty()) return +1;
        else if(buildTwoBranch == null || buildTwoBranch.getName().trim().isEmpty()) return -1;

        else if(buildOneBranch.getName().equals(buildTwoBranch.getName())) return 0;

        else if (branchIsMaster(buildOneBranch)) return -1;
        else if (branchIsMaster(buildTwoBranch)) return 1;

        return 0;

    };

    public static final Comparator<BuildPromotionEx> BUILD_ID_COMPARATOR = (buildOne, buildTwo) ->
            Math.toIntExact(buildTwo.getId() - buildOne.getId());


    public static Comparator<BuildPromotionEx> getFairProjectDistributionComparator(Map<String, List<BuildPromotionEx>> groupedByProjectId) {
        return (buildOne, buildTwo) -> {

            int positionAmongBuildsOfSameProjectForBuildOne = groupedByProjectId.get(buildOne.getProjectId()).indexOf(buildOne);
            int positionAmongBuildsOfSameProjectForBuildTwo = groupedByProjectId.get(buildTwo.getProjectId()).indexOf(buildTwo);

            return positionAmongBuildsOfSameProjectForBuildOne - positionAmongBuildsOfSameProjectForBuildTwo;

        };
    }
}