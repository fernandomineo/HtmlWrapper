package wrapperservice;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
class ExceptionWrapperAdvice {

        @ResponseBody
        @ExceptionHandler(ExceptionWrapper.NotValidUri.class)
        @ResponseStatus(HttpStatus.FORBIDDEN)
        String InvalidUriHandler(ExceptionWrapper.NotValidUri ex) {
            return ex.getMessage();
        }

        @ResponseBody
        @ExceptionHandler(ExceptionWrapper.UriNotProvided.class)
        @ResponseStatus(HttpStatus.NO_CONTENT)
        String UriNotProvided(ExceptionWrapper.UriNotProvided ex) {
            return ex.getMessage();
         }
}

