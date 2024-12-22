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
                        sendConfirmationEmail(existingUser, confirmationToken,"To confirm your account, please click here : " , "Welcome to Cura !!,Confirmation Email", "https://cura-addiction-recovery.netlify.app?token=");
                        usersRepository.save(existingUser);
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
                sendConfirmationEmail(newUser, confirmationToken,"To confirm your account, please click here : " , "Welcome to Cura !!,Confirmation Email", "https://cura-addiction-recovery.netlify.app?token=");
                usersRepository.save(newUser);

                return ResponseEntity.ok("Verify email by the link sent on your email address");
            }
        } catch (Exception e) {
            response = new Response<>(registerReq, e.getMessage(), "Failure");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    private void sendConfirmationEmail(Users user, String confirmationToken, String message, String subject, String url) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject(subject);
        mailMessage.setText("Hello Dear, " + user.getName() + "\n" +
                message + "\n" +
                url + confirmationToken);
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
                            "To reset the password of your account, please click here: ", "Reset Password!!", "https://cura-addiction-recovery.netlify.app/reset?token=");
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
