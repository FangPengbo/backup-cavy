package cc.focc.cavy;

import cc.focc.cavy.util.ExecUtil;
import cn.hutool.core.util.RuntimeUtil;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Arrays;
import java.util.List;

import static cc.focc.cavy.constant.ToolsConstant.*;

@MapperScan("cc.focc.cavy.mapper")
@EnableScheduling
@SpringBootApplication
public class CavyApplication {

	public static void main(String[] args) {
//		checkTools();
		SpringApplication.run(CavyApplication.class, args);
	}

	private static void checkTools() {
		for (String cmd : Arrays.asList(MYSQL_VERSION, MYSQLDUMP_VERSION, ZIP_VERSION)) {
			ExecUtil.exec(cmd);
		}
	}

}
