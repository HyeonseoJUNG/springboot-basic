package org.prgrms.orderApp.infrastructure;

import org.json.simple.parser.ParseException;
import org.prgrms.orderApp.domain.customer.model.Customer;
import org.prgrms.orderApp.domain.customer.repository.CustomerRepository;
import org.prgrms.orderApp.domain.voucher.model.Voucher;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class FileBlackListCustomerRepository implements CustomerRepository {
    private static String storagePath = "src/main/resources/storage/BlackList.csv";
    @Override
    public List<Customer> findAll() {
        File blackListFromFile = new File(storagePath);
        BufferedReader blackListContentsFromFile = null;
        List<Customer> blackList = new ArrayList<Customer>();
        try{
            blackListContentsFromFile = new BufferedReader(new FileReader(blackListFromFile));
            Charset.forName("UTF-8");
            String contentsFromFile = "";

            int index =0;
            while((contentsFromFile=blackListContentsFromFile.readLine()) != null) {
                index += 1;
                String[] contents = contentsFromFile.split(",");

                if (index == 1){
                    continue;
                }
                // Content Column => blackListCustomerId
                UUID blackListCustomerId = UUID.fromString(contents[0]);
                blackList.add(new Customer(blackListCustomerId));

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            //logger.error(e);
        } catch (IOException e) {
            e.printStackTrace();
            //logger.error(e);
        } finally {
            try {
                if(blackListContentsFromFile != null) {blackListContentsFromFile.close();}
            } catch (IOException e) {
                e.printStackTrace();
                //logger.error(e);
            }
        }
        return blackList;

    }

}