package com.fictio.parrot.thinking.enums;

import org.junit.Test;
import com.fictio.parrot.thinking.enums.RandomTest.Food.Coffee;
import com.fictio.parrot.thinking.enums.RandomTest.Food.Dessert;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RandomTest {
	enum Activity {
		SITTING,LYING,STANDING,HOPPING,RUNNING,DODGING,JUMPING,FLYING
	}

	@Test
	public void randTest() {
		for(int i = 0; i < 10; i++)
			log.debug("{}",EnumUtils.random(Activity.class));
	}

	// 接口组织枚举
	interface Food {
		enum Appetizer implements Food {
			SALAD,SOUP,SPRING_ROLLS;
		}
		enum MainCourse implements Food {
			LASAGNE, BURRITO, PAD_THAI,LENTILS,HUMMOUS,VINDALOO;
		}
		enum Dessert implements Food {
			TIRAMISE,GELATO,BLACK_FOREST_CAKE,FRUIT,GREME_CARAMEL;
		}
		enum Coffee implements Food {
			BLACK_COFFEE,DECAF_COFFEE,ESPRESSO,LATTE,CAPPUCCINO;
		}
	}	
	
	@Test
	public void typeOfFoodTest() {
		Food food = Dessert.BLACK_FOREST_CAKE;
		food = Coffee.BLACK_COFFEE;
		log.debug("FOOD: {}",food);
	}

	// "枚举的枚举"
	enum Course {
		APPETIZER(Food.Appetizer.class),
		MAINCOUSE(Food.MainCourse.class),
		DESSERT(Food.Dessert.class),
		COFFEE(Food.Coffee.class);
		private Food[] values;
		private Course(Class<? extends Food> kind) {
			values = kind.getEnumConstants();
		}
		public Food randomSelect() {
			return EnumUtils.random(values);
		}
	}	
	
	// 拼凑菜单
	@Test
	public void mealTest() {
		for(int i = 0; i < 5; i++) {
			log.debug("=====start=====");
			// 遍历Course 实现 枚举的枚举
			for(Course c : Course.values()) {
				Food food = c.randomSelect();
				log.debug("FOOD == > {}",food);
			}
			log.debug("=====end=====");
		}
	}
	
	// 枚举嵌套枚举
	enum SecurityCategory {
		STOCK(Security.Stock.class),
		BOND(Security.Bond.class);
		private Security [] values;
		private SecurityCategory (Class <? extends Security> kind) {
			values = kind.getEnumConstants();
		}
		public Security randomSelect() {
			return EnumUtils.random(values);
		}
		// 将接口组成一个公共类型
		interface Security {
			enum Stock implements Security { SHORT,LONG,MARGIN }
			enum Bond implements Security { MUNICIPAL,JUNK }
		}
	}
	
	@Test
	public void securityTest() {
		for(int i = 0; i < 10; i++) {
			SecurityCategory category = EnumUtils.random(SecurityCategory.class);
			log.debug("{} : {}",category,category.randomSelect());
		}
	}
	
	// 只是重新组织了代码,目的是更清晰的结构
	enum Course2 {
		APPETIZER(Food.Appetizer.class),
		MAINCOUSE(Food.MainCourse.class),
		DESSERT(Food.Dessert.class),
		COFFEE(Food.Coffee.class);
		private Food[] values;
		private Course2(Class<? extends Food> kind) {
			values = kind.getEnumConstants();
		}
		public Food randomSelect() {
			return EnumUtils.random(values);
		}
		public interface Food {
			enum Appetizer implements Food {
				SALAD,SOUP,SPRING_ROLLS;
			}
			enum MainCourse implements Food {
				LASAGNE, BURRITO, PAD_THAI,LENTILS,HUMMOUS,VINDALOO;
			}
			enum Dessert implements Food {
				TIRAMISE,GELATO,BLACK_FOREST_CAKE,FRUIT,GREME_CARAMEL;
			}
			enum Coffee implements Food {
				BLACK_COFFEE,DECAF_COFFEE,ESPRESSO,LATTE,CAPPUCCINO;
			}
		}	
	}		
	
	// 拼凑菜单2
	@Test
	public void meal2Test() {
		for(int i = 0; i < 5; i++) {
			log.debug("=====start=====");
			// 遍历Course 实现 枚举的枚举
			for(Course2 c : Course2.values()) {
				Course2.Food food = c.randomSelect();
				log.debug("FOOD2 == > {}",food);
			}
			log.debug("=====end=====");
		}
	}	
	
}
