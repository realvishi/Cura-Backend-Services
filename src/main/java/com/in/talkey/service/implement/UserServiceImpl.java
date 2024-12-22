package com.in.talkey.service.implement;

import com.in.talkey.dto.LoginDto;
import com.in.talkey.dto.LoginResponseDto;
import com.in.talkey.dto.RegisterDto;
import com.in.talkey.entity.Users;
import com.in.talkey.helper.Response;
import com.in.talkey.repository.UsersRepository;
import com.in.talkey.service.CloudinaryService;
import com.in.talkey.service.EmailService;
import com.in.talkey.service.JwtService;
import com.in.talkey.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;
import java.util.Optional;

@Component
public class UserServiceImpl implements UserService{

    private final UsersRepository usersRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final EmailService emailService;
    private final UserDetailsService userDetailsService;
    private final CloudinaryService cloudinaryService;


    @Autowired
    public UserServiceImpl (UsersRepository usersRepository, JwtService jwtService, AuthenticationManager authenticationManager, BCryptPasswordEncoder bCryptPasswordEncoder, EmailService emailService, UserDetailsService userDetailsService, CloudinaryService cloudinaryService){
        this.usersRepository = usersRepository;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.emailService = emailService;
        this.userDetailsService = userDetailsService;
        this.cloudinaryService = cloudinaryService;
    }


    @Override
    public ResponseEntity<?> Register(RegisterDto registerReq) {
        Response<RegisterDto> response;

        try {
            Optional<Users> existingUserOpt = usersRepository.findByEmail(registerReq.getEmail());

            if (existingUserOpt.isPresent()) {

                Users existingUser = existingUserOpt.get();
                if (existingUser.getIsEnable()) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The email is already in use!!");
                } else {

                    String confirmationToken = usersRepository.findConfirmationTokenByEmail(existingUser.getEmail())
                            .orElse(null);
                    if (jwtService.isTokenExpired(confirmationToken)) {
                        String newConfirmationToken = jwtService.generateToken(existingUser);
                        existingUser.setConfirmationToken(newConfirmationToken);
                        sendConfirmationEmail(existingUser, confirmationToken,"Thank you for signing up with Cura! We're excited to have you join our community.</p>" +
                           "            <p>To complete your registration, please click the button below:" , "Welcome to Cura! Confirm Your Account", "https://cura-addiction-recovery.netlify.app?token=", "Confirm My Account");
                        return ResponseEntity.ok("Verify email by the link sent on your email address");
                    } else {
                        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                                .body("This account is not yet validated. A verification link has been sent to your email. Please verify the user.");
                    }
                }
            } else {
                Users newUser = new Users();
                newUser.setEmail(registerReq.getEmail());
                newUser.setName(registerReq.getName());
                newUser.setPassword(bCryptPasswordEncoder.encode(registerReq.getPassword()));
                String[] cloudinaryRes = cloudinaryService.UploadImage(registerReq.getProfileImage(), registerReq.getEmail());
                newUser.setProfileImageUrl(cloudinaryRes[0]);
                newUser.setImageId(cloudinaryRes[1]);

                String confirmationToken = jwtService.generateToken(newUser);
                newUser.setConfirmationToken(confirmationToken);
                sendConfirmationEmail(newUser, confirmationToken,"Thank you for signing up with Cura! We're excited to have you join our community.</p>" +
                           "            <p>To complete your registration, please click the button below:" , "Welcome to Cura! Confirm Your Account", "https://cura-addiction-recovery.netlify.app?token=", "Confirm My Account");
                usersRepository.save(newUser);

                return ResponseEntity.ok("Verify email by the link sent on your email address");
            }
        } catch (Exception e) {
            response = new Response<>(registerReq, e.getMessage(), "Failure");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    public String createConfirmationEmailBody(String message, String url, String buttonText) {
        return "<!DOCTYPE html>" +
               "<html lang=\"en\">" +
               "<head>" +
               "    <meta charset=\"UTF-8\">" +
               "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
               "    <title>Confirmation Email</title>" +
               "    <style>" +
               "        body {" +
               "            font-family: 'Arial', sans-serif;" +
               "            background-color: #f9f9f9;" +
               "            color: #333;" +
               "            margin: 0;" +
               "            padding: 0;" +
               "            text-align: center;" +
               "        }" +
               "        .container {" +
               "            max-width: 600px;" +
               "            margin: 20px auto;" +
               "            background-color: #ffffff;" +
               "            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);" +
               "            border-radius: 8px;" +
               "            overflow: hidden;" +
               "        }" +
               "        .header {" +
               "            background-color: #4CAF50;" +
               "            color: #ffffff;" +
               "            padding: 20px 0;" +
               "        }" +
               "        .content {" +
               "            padding: 20px;" +
               "        }" +
               "        .content p {" +
               "            margin: 10px 0;" +
               "        }" +
               "        .button-container {" +
               "            margin-top: 20px;" +
               "        }" +
               "        .button {" +
               "            display: inline-block;" +
               "            padding: 10px 20px;" +
               "            background-color: #4CAF50;" +
               "            color: #ffffff;" +
               "            text-decoration: none;" +
               "            border-radius: 5px;" +
               "        }" +
               "        .button:hover {" +
               "            background-color: #45a049;" +
               "        }" +
               "    </style>" +
               "</head>" +
               "<body>" +
               "    <div class=\"container\">" +
               "        <div class=\"header\">" +
               "            <h1>Welcome to Cura!</h1>" +
               "        </div>" +
               "        <div class=\"content\">" +
               "            <p>" + message + "</p>" +
               "            <div class=\"button-container\">" +
               "                <a href=\"" + url + "\" class=\"button\">" + buttonText + "</a>" +
               "            </div>" +
               "        </div>" +
               "    </div>" +
               "</body>" +
               "</html>";
    }
    
    private void sendConfirmationEmail(Users user, String confirmationToken, String message, String subject, String url, String buttonText) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject(subject);
    
        String emailBody = createConfirmationEmailBody(message, url + confirmationToken, buttonText);
        mailMessage.setText(emailBody);
    
        emailService.Send(mailMessage);
    }
    


    @Override
    public ResponseEntity<?> Login(LoginDto authRequest) {
        try {
            Authentication authenticate = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
            );
            if (authenticate.isAuthenticated()) {
                Users user = usersRepository.findByEmail(authRequest.getEmail())
                        .orElseThrow(() -> new UsernameNotFoundException("User not found in our Database"));
                String token = jwtService.generateToken(user);
                LoginResponseDto res = new LoginResponseDto(user.getId(),user.getName(), user.getEmail(), user.getProfileImageUrl(), token);
                return ResponseEntity.ok(res);
            }
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Bad credentials");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed");
    }

    @Override
    public ResponseEntity<?> Verify(String token) {
        try {
            System.out.println("token:" + token);
            String email = jwtService.extractUsername(token);
            System.out.println(email);
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            Users user = usersRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("This is not a Valid Token"));
            System.out.println(userDetails);
            if(jwtService.isTokenValid(token, userDetails)){
                user.setIsEnable(true);
                user.setConfirmationToken(null);
                usersRepository.save(user);
                return ResponseEntity.ok("Your Email is Verified");
            }
            else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("the token is not valid");
            }
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }

    @Override
    public ResponseEntity<?> ForgotPassword(String email) {
        try {
            Optional<Users> existingUserOpt = usersRepository.findByEmail(email);

            if (existingUserOpt.isPresent() && existingUserOpt.get().getIsEnable()) {
                Users user = existingUserOpt.get();
                String confirmationToken = user.getConfirmationToken();

                if (confirmationToken != null && !jwtService.isTokenExpired(confirmationToken)) {
                    return ResponseEntity.status(HttpStatus.ALREADY_REPORTED)
                            .body("A reset email has already been sent. Please use it to reset your password.");
                } else {
                    confirmationToken = jwtService.generateToken(user);
                    user.setConfirmationToken(confirmationToken);
                    sendConfirmationEmail(user, confirmationToken,
                            "It seems you've requested to reset your password. Please click the button below to proceed:", "Reset Your Cura Password", "https://cura-addiction-recovery.netlify.app/reset?token=" , "Reset My Password");
                    usersRepository.save(user);
                    return ResponseEntity.ok("Password reset link has been sent to your email.");
                }
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("This email is not verified. Please verify it for further use.");
            }

        } catch (Exception e) {
            System.err.println("ERROR: \n" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while processing your request. Please try again later.");
        }
    }

    @Override
    public ResponseEntity<?> ResetPassword(String newPassword, String token) {
        try {
             if (jwtService.isTokenExpired(token)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("The token is expired, please re-try resetting your password.");
            }
            String email = jwtService.extractUsername(token);
            Users user = usersRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("Token is not Valid"));

            if (Objects.equals(user.getConfirmationToken(), token)) {
                user.setPassword(bCryptPasswordEncoder.encode(newPassword));
                user.setConfirmationToken(null);
                usersRepository.save(user);
                return ResponseEntity.ok("Password Reset Successfully");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("The Token is Not Valid");
            }
        } catch (Exception e) {
            System.err.println("ERROR: \n" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while processing your request. Please try again later.");
        }
    }



}
