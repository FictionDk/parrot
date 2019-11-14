package com.fictio.parrot.thinking.enums;

import java.util.EnumSet;
import org.junit.Test;
import lombok.extern.slf4j.Slf4j;
import static com.fictio.parrot.thinking.enums.AlarmPoints.*;

@Slf4j
public class EnumSets {
	@Test
	public void test() {
		EnumSet<AlarmPoints> points = EnumSet.noneOf(AlarmPoints.class);
		points.add(LOBBY);
		log.debug("1: {}",points);
		
		// of方法5个(含5个)参数使用重载,超过5个使用可变参数方法
		points.addAll(EnumSet.of(KITCHEN,OFFICE1,BATHROOM));
		log.debug("2: {}",points);
		
		// enums在set里保留了顺序
		points.addAll(EnumSet.allOf(AlarmPoints.class));
		log.debug("3: {}",points);
		
		points.removeAll(EnumSet.of(OFFICE2,OFFICE3));
		log.debug("4: {}",points);
		
		points.removeAll(EnumSet.range(LOBBY, BATHROOM));
		log.debug("5: {}",points);
		
		//取反
		points = EnumSet.complementOf(points);
		log.debug("6: {}",points);
		
	}
	
}
