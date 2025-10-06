package com.example.demo.service;

import com.example.demo.entity.UserDetails;
import com.example.demo.repository.UserDetailsRepository;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import org.bouncycastle.util.Times;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;


@Service
public class PdfService {

    private final UserDetailsRepository userDetailsRepository;

    public PdfService(UserDetailsRepository userDetailsRepository) {
        this.userDetailsRepository = userDetailsRepository;
    }


   // Method to generate pdf and send the file as response
    public void  generateEmployeePdf(OutputStream outputStream) throws IOException{
        List<UserDetails> employees = userDetailsRepository.findAll();


        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.A4.rotate());

        PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        document.setFont(font);
        document.add(new Paragraph("Employee Details").setBold().setFontSize(18));
        document.setMargins(40, 40, 40, 40); // top, right, bottom, left

        Table table = new Table(new float[]{2, 1, 1, 5, 2, 2, 2, 2, 2});
        table.setWidth(UnitValue.createPercentValue(100));
        table.useAllAvailableWidth(); // stretches proportionally

        table.addHeaderCell("Employee_Id").setBold();
        table.addHeaderCell("First_Name").setBold();
        table.addHeaderCell("Last_Name").setBold();
        table.addHeaderCell("Email").setBold();
        table.addHeaderCell("Phone_Number").setBold();
        table.addHeaderCell("Date_Of_Birth").setBold();
        table.addHeaderCell("Joining_Date").setBold();
        table.addHeaderCell("Department");
        table.addHeaderCell("Designation");

        for(UserDetails employee:employees){
            table.addCell(String.valueOf(employee.getEmployeeId()));
            table.addCell(employee.getFirstName());
            table.addCell(employee.getLastName());
            table.addCell(employee.getEmail()).setFontSize(9).setKeepTogether(false);
            table.addCell(employee.getPhoneNumber());
            table.addCell(String.valueOf(employee.getDateOfBirth()));
            table.addCell(String.valueOf(employee.getJoiningDate()));
            table.addCell(employee.getDepartment());
            table.addCell(employee.getDesignation());
        }
        document.add(table);
        document.close();

    }
}
