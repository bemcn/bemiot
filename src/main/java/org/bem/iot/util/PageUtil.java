package org.bem.iot.util;

public class PageUtil {
	/**
	 * 获取分页查询开始游标位置
	 * @param index 当前页码
	 * @param size 每页显示量
	 * @return 返回开始查询游标位置
	 */
	public static int getQueryStartNo(int index, int size) {
		return (index-1) * size;
	}

	/**
	 * 获取分页总页数
	 * @param total 总数据量
	 * @param size 每页显示量
	 * @return 返回分页总页数
	 */
	public static long getQueryPageNum(long total, long size) {
		long pages = 1L;
		if(total > 0) {
			pages = (total-1L)/size + 1L;
		}
		return pages;
	}
	
}
