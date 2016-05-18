package com.shixi.gaodun.inf;

import java.util.Comparator;

import com.shixi.gaodun.model.domain.CityBean;

/**
 * @author ronger
 * @date:2015-10-27 下午3:48:25
 */
public class PinyinComparator implements Comparator<CityBean> {
	@Override
	public int compare(CityBean o1, CityBean o2) {
		if (o1.getSortLetters().equals("@") || o2.getSortLetters().equals("#")) {
			return -1;
		} else if (o1.getSortLetters().equals("#") || o2.getSortLetters().equals("@")) {
			return 1;
		} else {
			return o1.getSortLetters().compareTo(o2.getSortLetters());
		}
	}

}
