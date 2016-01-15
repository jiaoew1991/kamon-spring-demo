package kamon.annotation.examples.java;

import kamon.annotation.EnableKamon;
import kamon.annotation.Trace;
import kamon.trace.Tracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

// tag:traces:start
@EnableKamon
@Controller
@RequestMapping("/java/users")
public class JavaUsersController {

    private static Logger logger = LoggerFactory.getLogger(JavaUsersController.class);

    @Autowired
    JavaUsersService usersService;

    @Trace("ListAllUsers")
    @RequestMapping(method = RequestMethod.GET)
    public @ResponseBody String listUsers() {
        logger.debug("trace context {}", Tracer.currentContext());
        logger.debug("trace token {}", Tracer.currentContext().token());
        logger.debug("hello list all users");
        return usersService.findUsers();
    }
}
// tag:traces:end