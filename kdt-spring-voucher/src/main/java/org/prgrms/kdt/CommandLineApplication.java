package org.prgrms.kdt;

import org.prgrms.kdt.controller.VoucherController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class CommandLineApplication {

    public static void main(String[] args) {
        AnsiOutput.setEnabled(AnsiOutput.Enabled.ALWAYS);


//        var springApplication = new SpringApplication(CommandLineApplication.class);
////        springApplication.setAdditionalProfiles("local");
//        var applicationContext = springApplication.run(args);
//        VoucherController voucherController = new VoucherController();
//        voucherController.run();

        SpringApplication.run(CommandLineApplication.class, args);


    }

}
