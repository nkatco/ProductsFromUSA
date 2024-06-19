package com.example.productsfromusa.controllers;

import com.example.productsfromusa.models.ShortLink;
import com.example.productsfromusa.models.Statistic;
import com.example.productsfromusa.restServices.ShortLinkService;
import com.example.productsfromusa.services.data.StatisticService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RequestMapping("/link")
@RestController
public class LinkController {

    @Autowired
    private ShortLinkService shortLinkService;
    @Autowired
    private StatisticService statisticService;

    @GetMapping("/{id}")
    public void redirectToOriginalUrl(@PathVariable("id") String id, HttpServletRequest request, HttpServletResponse response) throws IOException {
        ShortLink shortLink = shortLinkService.getShortLinkById(id);
        if (shortLink != null) {
            String originalUrl = shortLink.getLink();
            if (originalUrl != null) {
                if(statisticService.getAllByIpAndLinkId(request.getRemoteAddr(), id) == null) {
                    Statistic statistic = new Statistic();
                    statistic.setCategory(shortLink.getAnonsPost().getAnons().getCategory());
                    statistic.setChannel(shortLink.getAnonsPost().getAnons().getToken().getChannel());
                    statistic.setIp(request.getRemoteAddr());
                    statistic.setLinkId(shortLink.getId());
                    statisticService.saveStatistic(statistic);
                } else if (statisticService.getAllByIpAndLinkId(request.getRemoteAddr(), id).size() == 0) {
                    Statistic statistic = new Statistic();
                    statistic.setCategory(shortLink.getAnonsPost().getAnons().getCategory());
                    statistic.setChannel(shortLink.getAnonsPost().getAnons().getToken().getChannel());
                    statistic.setIp(request.getRemoteAddr());
                    statistic.setLinkId(shortLink.getId());
                    statisticService.saveStatistic(statistic);
                }
                response.sendRedirect(originalUrl);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}

