package com.v5ent;


import static spark.Spark.port;

import net.matthew.Static_Value;

/**
 * 
 * @author ZJU 导师：罗 德明 作者：朱 逸清
 * @see 基于国内市场资产证券化风险事件分析与区块链应用的金融科技设计
 * 
 *
 */
public class A_Value_SparkWeb {
	public static void main(String[] args) {
		port(Static_Value.ABS_A_VALUE_CHAIN_PORT);
		CommonMethod.initial();
	}
}
