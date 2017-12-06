package org.lilliput.chronograph.common;

import org.lilliput.chronograph.common.Tokens.AC;

/**
 * Copyright (C) 2016-2017 Jaewook Byun
 * 
 * ChronoGraph: Temporal Property Graph and Traversal Language
 * 
 * Temporal Relationship
 * 
 * @author Jaewook Byun, Ph.D candidate
 * 
 *         Korea Advanced Institute of Science and Technology (KAIST)
 * 
 *         Real-time Embedded System Laboratory (RESL)
 * 
 *         bjw0829@kaist.ac.kr, bjw0829@gmail.com
 * 
 */
public class TemporalRelationship {

	public static boolean isPassingComparator(LongInterval left, AC ss, AC se, AC es, AC ee, LongInterval right) {
		if (ss != null && !isPassingComparator(left.getStart(), ss, right.getStart()))
			return false;
		if (se != null && !isPassingComparator(left.getStart(), se, right.getEnd()))
			return false;
		if (es != null && !isPassingComparator(left.getEnd(), es, right.getStart()))
			return false;
		if (ee != null && !isPassingComparator(left.getEnd(), ee, right.getEnd()))
			return false;
		return true;
	}

	public static boolean isPassingComparator(long left, AC comparator, long right) {
		if (comparator == null)
			return true;
		if (comparator.equals(AC.$gte) && left <= right) {
			return true;
		} else if (comparator.equals(AC.$gt) && left < right) {
			return true;
		} else if (comparator.equals(AC.$eq) && left == right) {
			return true;
		} else if (comparator.equals(AC.$lte) && left <= right) {
			return true;
		} else if (comparator.equals(AC.$lt) && left < right) {
			return true;
		} else
			return false;
	}
}
