package com.negocionaarea.mobile_api.service;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ResendEmailService {

    private final Resend resend;

    public ResendEmailService(@Value("${resend.api.key}") String apiKey) {
        this.resend = new Resend(apiKey);
    }

    public void enviar(String destinatario, String assunto, String htmlBody) {
        try {
            CreateEmailOptions params = CreateEmailOptions.builder()
                    .from("onboarding@resend.dev")
                    .to(destinatario)
                    .subject(assunto)
                    .html(htmlBody)
                    .build();

            CreateEmailResponse response = resend.emails().send(params);
            System.out.println("✅ Email enviado via Resend. ID: " + response.getId());

        } catch (ResendException e) {
            System.out.println("❌ Erro ao enviar email via Resend: " + e.getMessage());
        }
    }
}