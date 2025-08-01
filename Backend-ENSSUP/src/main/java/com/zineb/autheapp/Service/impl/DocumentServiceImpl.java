package com.zineb.autheapp.Service.impl;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import com.zineb.autheapp.Service.interfaces.DocumentService;
import com.zineb.autheapp.dao.entities.AppConvention;
import com.zineb.autheapp.dao.entities.Documents;
import com.zineb.autheapp.dao.repository.ConventionRepository;
import com.zineb.autheapp.dao.repository.DocumentRepository;
import com.zineb.autheapp.dto.ConventionResponseDTO;
import com.zineb.autheapp.mapper.ConventionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService  {
    private final DocumentRepository documentRepository;
    private final ConventionRepository conventionRepository;
    private  final MinioService minioService;
    private final ConventionMapper conventionMapper;
    @Override
    public void addDocument(Long conventionId, String nom) {
        boolean exists = documentRepository.existsByNom(nom);
        if (exists) {
            throw new RuntimeException("Le nom du fichier existe déjà. Veuillez en choisir un autre.");
        }
        AppConvention convention = conventionRepository.findById(conventionId)
                .orElseThrow(() -> new RuntimeException("Convention non trouvée"));
        ConventionResponseDTO dto = conventionMapper.toResponseDTO(convention);
        byte[] pdfBytes = generatePdf(dto);
       minioService.uploadPdfToMinio(pdfBytes, nom);
        Documents document = new Documents();
        document.setConvention(convention);
        document.setNom(nom);

        documentRepository.save(document);
    }

    private byte[] generatePdf(ConventionResponseDTO dto) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLUE);
            Paragraph title = new Paragraph("Détails de la Convention", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" "));

            document.add(new Paragraph("ID : " + dto.getId()));
            document.add(new Paragraph("Titre : " + safe(dto.getTitle())));
            document.add(new Paragraph("Numéro : " + safe(dto.getConventionNumber())));
            document.add(new Paragraph("Objet : " + safe(dto.getObject())));
            document.add(new Paragraph("Type : " + safe(dto.getTypeCode())));
            document.add(new Paragraph("Partenaires : " + safe(dto.getPartners())));
            document.add(new Paragraph("Date de signature : " + safe(dto.getSignatureDate())));
            document.add(new Paragraph("Date de début : " + safe(dto.getStartDate())));
            document.add(new Paragraph("Date de fin : " + safe(dto.getEndDate())));
            document.add(new Paragraph("Créée le : " + safe(dto.getCreatedAt())));

            if (dto.getCreatedBy() != null) {
                document.add(new Paragraph("Créé par : " + safe(dto.getCreatedBy().getUsername())));
            }

            document.add(new Paragraph(" "));

            String type = dto.getTypeCode();
            Map<String, String> fields = dto.getCustomFields();
            if ("ECHANGES".equalsIgnoreCase(type)) {
                document.add(new Paragraph(" Informations Échange",FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)));
                document.add(new Paragraph(">> Nature de l'échange : " + safe(dto.getNatureEchange())));
                document.add(new Paragraph(">> Modalité de l'échange : " + safe(dto.getModaliteEchange())));
                document.add(new Paragraph(">> Logistique : " + safe(dto.getLogistique())));
                document.add(new Paragraph(">> Assurance Responsabilité : " + safe(dto.getAssuranceResponsabilite())));
                document.add(new Paragraph(">> Renouvellement : " + safe(dto.getRenouvellement())));
                document.add(new Paragraph(">> Résiliation : " + safe(dto.getResiliation())));
            } else if ("ACCORD".equalsIgnoreCase(type)) {
                document.add(new Paragraph(" Informations Accord",FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)));
                document.add(new Paragraph(">> Périmètre : " + safe(dto.getPerimetre())));
                document.add(new Paragraph(">> Modalité : " + safe(dto.getModalite())));
            }
            if (fields != null && !fields.isEmpty()) {
                document.add(new Paragraph(" "));
                Paragraph subTitle = new Paragraph("Champs personnalisés :", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14));
                subTitle.setSpacingBefore(10);
                document.add(subTitle);

                for (Map.Entry<String, String> entry : fields.entrySet()) {
                    document.add(new Paragraph(entry.getKey() + " : " + entry.getValue()));
                }
            }
            document.close();
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Erreur PDF : " + e.getMessage());
        }
    }

    private String safe(Object value) {
        return value != null ? value.toString() : "N/A";
    }


}
