/*
 * Copyright (c) 2018, se-2018 and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 */

package judge;

import solution.Solution;
import jigsaw.JigsawNode;
import jigsaw.Jigsaw;

import java.util.List;
import java.util.Scanner;

/**
 *
 * <p>This class judge
 * <a href="https://se-2018.github.io/Stage3--NPuzzle">
 * N-Puzzle</a> experimental task.
 * @author  se-2018
 */
public class main {
    private final static long NANOSECOND_PER_MICROSECOND = 1000000L;

    private final static int ASTAR_UPPER_LIMIT = 29000;
    private final static int ASTAR_LOWER_LIMIT = 1000;
    private final static long ASTAR_LOWER_LIMIT_TIME = 15;
    private final static int ASTAR_TEST_TIME = 3;

    private final static int BFS_SCORE = 2;
    private final static int ASTAR_SCORE = 12;
    private final static int ASTAR_MAX_SCORE = 14;

    private final static JigsawNode DEST_NODE = new JigsawNode(new int[]{25,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,0});
    private static int bfsLength;
    private static JigsawNode bfsNode;
    private static JigsawNode[] aStarNodes = new JigsawNode[ASTAR_TEST_TIME];


    private static JigsawNode getJigsawNode(Scanner scan) {
        int len = JigsawNode.getDimension() * JigsawNode.getDimension();
        int[] state = new int[len + 1];
        for (int i = 0; i <= len; i++) {
            state[i] = scan.nextInt();
        }
        return new JigsawNode(state);
    }

    public static int calBFSScore(int length) {
        if (length == bfsLength) {
            return BFS_SCORE;
        }
        return 0;
    }

    public static int calAStarScore(int rawScore) {
        if (rawScore > ASTAR_MAX_SCORE * 2) {
            return ASTAR_MAX_SCORE;
        }
        if (rawScore >= ASTAR_MAX_SCORE) {
            return rawScore % ASTAR_MAX_SCORE
                + rawScore / ASTAR_MAX_SCORE * (ASTAR_SCORE / ASTAR_TEST_TIME);
        }
        return rawScore;
    }

    public static int estimateAStarScore(int searchedNodesNum, long runTime) {
        if (searchedNodesNum < ASTAR_LOWER_LIMIT && runTime <= ASTAR_LOWER_LIMIT_TIME) {
            return ASTAR_MAX_SCORE;
        }
        if (searchedNodesNum < ASTAR_UPPER_LIMIT) {
            return ASTAR_SCORE / ASTAR_TEST_TIME;
        }
        return 0;
    }


    public static int TestBFS(String runtimeToken) {
        JigsawNode destNode = DEST_NODE;
        JigsawNode startNode = bfsNode;

        Jigsaw solution = new Solution();
        boolean solved = false;

        try {
            final long startTime = System.nanoTime();

            solved = solution.BFSearch(new JigsawNode(startNode), new JigsawNode(destNode));

            final long duration = System.nanoTime() - startTime;
            System.out.println("\nBFS Total " + runtimeToken + ":" + duration / NANOSECOND_PER_MICROSECOND + "ms");
        } catch (Throwable th) {
            th.printStackTrace();
            System.exit(0);
        }

        if (!solved) {
            return 0;
        }
        List<JigsawNode> solutionPath = solution.getPath();
        if (!Jigsaw.isValidPath(solutionPath, startNode, destNode)) {
            return 0;
        }

        return calBFSScore(solutionPath.size() - 1);
    }

    public static int TestAStar(String runtimeToken) {
        long totalTime = 0;
        int rawScore = 0;

        for (int i = 0; i < ASTAR_TEST_TIME; i++) {
            final long startTime = System.nanoTime();

            int searchedNodesNum = TestAStar(aStarNodes[i], DEST_NODE);

            final long duration = System.nanoTime() - startTime;
            System.out.println("\nAStar " + runtimeToken + ":" + duration / NANOSECOND_PER_MICROSECOND + "ms");
            totalTime += duration;

            rawScore += estimateAStarScore(searchedNodesNum, duration / NANOSECOND_PER_MICROSECOND);
        }

        System.out.println("\nAStar Total " + runtimeToken + ":" + totalTime / NANOSECOND_PER_MICROSECOND + "ms");

        return calAStarScore(rawScore);
    }

    public static int TestAStar(JigsawNode startNode, JigsawNode destNode) {
        Jigsaw solution = new Solution();

        try {
            if (!solution.ASearch(new JigsawNode(startNode), new JigsawNode(destNode))) {
                return Integer.MAX_VALUE;
            }
        } catch (Throwable th) {
            th.printStackTrace();
            System.exit(0);
        }

        List<JigsawNode> solutionPath = solution.getPath();
        if (!Jigsaw.isValidPath(solutionPath, startNode, destNode)) {
            return Integer.MAX_VALUE;
        }

        return solution.getSearchedNodesNum();
    }


    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        String scoreToken = scan.next();
        String runtimeToken = scan.next();
        bfsLength = scan.nextInt();
        bfsNode = getJigsawNode(scan);
        for (int i = 0; i < ASTAR_TEST_TIME; i++) {
            aStarNodes[i] = getJigsawNode(scan);
        }

        System.out.println("\n" + scoreToken + ":" + (TestBFS(runtimeToken) + TestAStar(runtimeToken)));
    }
}
