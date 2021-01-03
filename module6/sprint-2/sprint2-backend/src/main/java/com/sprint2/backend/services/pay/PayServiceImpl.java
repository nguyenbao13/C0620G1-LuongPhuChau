package com.sprint2.backend.services.pay;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import com.sprint2.backend.entity.Customer;
import com.sprint2.backend.entity.Invoice;
import com.sprint2.backend.entity.MemberCard;
import com.sprint2.backend.repository.InvoiceRepository;
import com.sprint2.backend.repository.MemberCardRepository;

@Service
public class PayServiceImpl implements PaySerVice {
    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private MemberCardRepository memberCardRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    /*
     * get all member card of customer currently logged in database
     * @param idCustomer
     * @return ResponseEntity<List<MemberCard>>
     * */
    @Override
    public List<MemberCard> findByCustomerID(Long id) {
        return this.memberCardRepository.findByCar_Customer_Id(id);
    }

    /*
     * update member card after customer pay and send mail notification for customer
     * @param money, memberCardList
     * void
     * */
    @Override
    public void updateMemberCardAfterPay(Double money, List<Long> memberCardList) {
        List<MemberCard> memberCardListAfterPay = new ArrayList<>();
        Customer customer = null;
        Invoice invoice = new Invoice();

        // update member card :
        for (Long element : memberCardList) {
            MemberCard memberCard = this.memberCardRepository.findById(element).orElse(null);
            if (memberCard != null) {
                memberCardListAfterPay.add(memberCard);
                if (memberCard.getMemberCardType().getMemberTypeName().equals("Tuần")) {
                    memberCard.setEndDate(memberCard.getEndDate().plusDays(7));
                } else if (memberCard.getMemberCardType().getMemberTypeName().equals("Tháng")) {
                    memberCard.setEndDate(memberCard.getEndDate().plusMonths(1));
                } else {
                    memberCard.setEndDate(memberCard.getEndDate().plusYears(1));
                }
                this.memberCardRepository.save(memberCard);

                // create invoice :
                if (customer == null) {
                    customer = memberCard.getCar().getCustomer();
                    invoice.setTotalAmount(money);
                    invoice.setCustomer(customer);
                    invoice.setPayDate(LocalDateTime.now());
                    this.invoiceRepository.save(invoice);
                }
            }
        }

        // send mail notification for customer after pay complete :
        try {
            MimeMessage message = this.emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
            String nameCustomer = memberCardListAfterPay.get(0).getCar().getCustomer().getFullName();

            helper.setTo(memberCardListAfterPay.get(0).getCar().getCustomer().getEmail());
            helper.setSubject("Thông báo gia hạn vé xe");
            StringBuilder mailContent = new StringBuilder("<!DOCTYPE html>\n" +
                    "<html lang=\"en\">\n" +
                    "<head>\n" +
                    "  <meta charset=\"UTF-8\">\n" +
                    "  <title>Mail</title>\n" +
                    "  <style>\n" +
                    "    .bodyMail {\n" +
                    "      margin-top: 1%;\n" +
                    "    }\n" +
                    "\n" +
                    "    h5 {\n" +
                    "      color: white;\n" +
                    "      background-color: green;\n" +
                    "      text-align: center\n" +
                    "    }\n" +
                    "\n" +
                    "    p {\n" +
                    "      margin: 1% 0;\n" +
                    "    }\n" +
                    "\n" +
                    "    table {\n" +
                    "      border: 1px solid;\n" +
                    "      border-collapse: separate;\n" +
                    "      width: 90%\n" +
                    "    }\n" +
                    "\n" +
                    "    td, th {\n" +
                    "      border: 1px solid;\n" +
                    "      text-align: center;\n" +
                    "    }\n" +
                    "\n" +
                    "    span {\n" +
                    "      color: red;\n" +
                    "    }\n" +
                    "\n" +
                    "    .autoMail {\n" +
                    "      color: red;\n" +
                    "    }\n" +
                    "  </style>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "<div class=\"container-fluid\">\n" +
                    "  <div class=\"row\">\n" +
                    "    <div class=\"col-sm-3\"></div>\n" +
                    "    <div class=\"col-sm-6 bodyMail\">\n" +
                    "      <h5>\n" +
                    "        Công ty TNHH C06Parking thông báo gia hạn vé xe thành công\n" +
                    "      </h5>\n" +
                    "      <p>Kính gửi quý khách: <span>" + nameCustomer + "</span></p>\n" +
                    "      <p>Sau đây là danh sách vé xe đã gia hạn thành công của quý khách : </p>\n" +
                    "      <div class=\"row\">\n" +
                    "        <div class=\"container-xl\">\n" +
                    "          <div class=\"table-responsive\">\n" +
                    "            <div class=\"table-wrapper\">\n" +
                    "              <table>\n" +
                    "                <tr>\n" +
                    "                  <th>Biển số xe</th>\n" +
                    "                  <th>Loại vé</th>\n" +
                    "                  <th>Ngày hết hạn sau khi cập nhật</th>\n" +
                    "                </tr>\n");
            for (MemberCard memberCardAfterPay : memberCardListAfterPay) {
                mailContent.append("<tr>\n");
                mailContent.append("<td>");
                mailContent.append(memberCardAfterPay.getCar().getPlateNumber());
                mailContent.append("</td>");
                mailContent.append("<td>");
                mailContent.append(memberCardAfterPay.getMemberCardType().getMemberTypeName());
                mailContent.append("</td>");
                mailContent.append("<td>");
                mailContent.append(memberCardAfterPay.getEndDate()
                        .format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                mailContent.append("</td>");
                mailContent.append("</tr>");
            }

            mailContent.append("</table>\n" +
                    "            </div>\n" +
                    "          </div>\n" +
                    "        </div>\n" +
                    "      </div>\n" +
                    "      <p class=\"autoMail\">P/s : Đây là thư thông báo tự động. " +
                    "Quý khách vui lòng không trả lời thư này!</p>\n" +
                    "    </div>\n" +
                    "    <div class=\"col-sm-3\"></div>\n" +
                    "  </div>\n" +
                    "</div>\n" +
                    "</body>\n" +
                    "</html>\n");

            helper.setText(String.valueOf(mailContent), true);
            this.emailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    /*
     * create signature for MoMo
     * @param money, requestID
     * @return Signature
     * */
    @Override
    public String createSignature(String money, String requestID) {
        String signature;
        String data = "partnerCode=MOMOBKUN20180529&accessKey=klm05TvNBzhg7h7j&requestId=" + requestID
                + "&amount=" + money +
                "&orderId=" + requestID + "&orderInfo=test thanh toan&returnUrl=https://momo.vn/" +
                "&notifyUrl=https://momo.vn/&extraData=merchantName=Payment";
        try {
            SecretKeySpec signingKey =
                    new SecretKeySpec("at67qH6mk8w5Y1nAyMoYKMWACiEi2bsa".getBytes(StandardCharsets.UTF_8),
                            "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(signingKey);
            byte[] rawHmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            byte[] hexArray = {(byte) '0', (byte) '1', (byte) '2', (byte) '3', (byte) '4', (byte) '5',
                    (byte) '6', (byte) '7', (byte) '8', (byte) '9', (byte) 'a', (byte) 'b', (byte) 'c',
                    (byte) 'd', (byte) 'e', (byte) 'f'};
            byte[] hexChars = new byte[rawHmac.length * 2];
            for (int j = 0; j < rawHmac.length; j++) {
                int v = rawHmac[j] & 0xFF;
                hexChars[j * 2] = hexArray[v >>> 4];
                hexChars[j * 2 + 1] = hexArray[v & 0x0F];
            }
            signature = new String(hexChars);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        System.out.println(signature);
        return signature;
    }
}
