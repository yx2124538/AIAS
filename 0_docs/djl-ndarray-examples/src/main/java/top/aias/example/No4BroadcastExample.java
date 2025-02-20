package top.aias.example;

import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDManager;

/**
 * NDarray Indexing and Slicing
 * NDArray的索引和切片
 *
 * @author Calvin
 * Mail: 179209347@qq.com
 */

public final class No4BroadcastExample {

    private No4BroadcastExample() {
    }

    public static void main(String[] args) {
        try (NDManager manager = NDManager.newBaseManager()) {
            // 1. Two arrays a and b have the same shape.
            // 1. 两个数组 a 和 b 具有相同的形状。
            NDArray x = manager.create(new int[]{1, 2, 3, 4});
            NDArray y = manager.create(new int[]{10, 20, 30, 40});
            NDArray z = x.mul(y);
            System.out.println(z.toDebugString(100, 10, 100, 100, true));

            // 2. Broadcasting mechanism is automatically triggered when the shapes of the two arrays in an operation are different.
            // 2. 当操作中两个数组的形状不同时，广播机制会自动触发。
            x = manager.create(new int[][]{{0, 0, 0}, {10, 10, 10}, {20, 20, 20}, {30, 30, 30}});
            y = manager.create(new int[]{1, 2, 3});
            z = x.add(y);
            System.out.println(z.toDebugString(100, 10, 100, 100, true));
        }

    }
}
