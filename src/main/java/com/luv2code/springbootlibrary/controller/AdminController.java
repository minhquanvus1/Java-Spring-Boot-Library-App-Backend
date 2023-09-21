package com.luv2code.springbootlibrary.controller;

import com.luv2code.springbootlibrary.requestmodels.AddBookRequest;
import com.luv2code.springbootlibrary.service.AdminService;
import com.luv2code.springbootlibrary.utils.ExtractJWT;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("http://localhost:3000")
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private AdminService adminService;

    // constructor dependency injection
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/secure/add/book")
    public void postBook(@RequestHeader(value = "Authorization") String token, @RequestBody AddBookRequest addBookRequest) throws Exception {
        // check if the user who calls this API, is actually an admin or not (ONLY admin can call this API)
        String admin = ExtractJWT.payloadJWTExtraction(token, "\"userType\"");
        if(!admin.equals("admin") || admin == null) {
            throw new Exception("Administration page only!");
        }
        adminService.postBook(addBookRequest);
    }

    @PutMapping("/secure/increase/book/quantity")
    public void increaseBookQuantity(@RequestHeader(value = "Authorization") String token, @RequestParam Long bookId) throws Exception {
       // check if the user who calls this API is an admin
        String admin = ExtractJWT.payloadJWTExtraction(token, "\"userType\"");
        if(!admin.equals("admin") || admin == null) {
            throw new Exception("Administration page only!");
        }

        adminService.increaseBookQuantity(bookId);
    }
}
