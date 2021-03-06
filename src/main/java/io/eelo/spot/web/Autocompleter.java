package io.eelo.spot.web;

import io.eelo.spot.data.Params;
import io.eelo.spot.data.SearchQuery;
import io.eelo.spot.threadPool.ThreadPool;
import org.xml.sax.SAXException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import static io.eelo.spot.data.Preferences.autocompletersById;
import static io.eelo.spot.data.Preferences.enginesById;

public class Autocompleter extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        final Params params = (Params) req.getAttribute("params");
        final SearchQuery searchQuery = params.generateSearchQuery();
        enginesById.values().forEach(engine -> ThreadPool.run(() -> engine.prepareSearch(searchQuery)));
        final String id = params.getAutocompleter();
        final PrintWriter writer = resp.getWriter();
        resp.setContentType("text/plain;charset=UTF-8");
        try {
            final List<String> suggestions = autocompletersById.get(id).search(params);
            suggestions.forEach(writer::println);
        } catch (ParserConfigurationException | SAXException e) {
            e.printStackTrace();
        }
    }
}
