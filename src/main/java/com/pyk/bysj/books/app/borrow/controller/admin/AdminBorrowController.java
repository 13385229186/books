package com.pyk.bysj.books.app.borrow.controller.admin;

import com.pyk.bysj.books.model.dto.BorrowStatusDTO;
import com.pyk.bysj.books.utils.ResponseData;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestControllerw
@RequestMapping("/admin")
public class AdminBorrowController {

//  @PostMapping("/setBorrowStatus")
//  public ResponseData setBorrowStatus(
//          @RequestBody @Valid BorrowStatusDTO borrowStatusDTO
//  ){
//
//
//    return null;
//  }

//  public ResponseData handleBorrow(
//          @RequestParam("id") @Positive Integer id
//
//  )
}
