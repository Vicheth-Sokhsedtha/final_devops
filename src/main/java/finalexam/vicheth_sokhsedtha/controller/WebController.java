package finalexam.vicheth_sokhsedtha.controller;

import finalexam.vicheth_sokhsedtha.model.BarcodeType;
import finalexam.vicheth_sokhsedtha.model.Profile;
import finalexam.vicheth_sokhsedtha.model.ProfileType;
import finalexam.vicheth_sokhsedtha.model.Template;
import finalexam.vicheth_sokhsedtha.service.PhotoStorageService;
import finalexam.vicheth_sokhsedtha.service.ProfileService;
import finalexam.vicheth_sokhsedtha.service.RegistrationNumberGenerator;
import finalexam.vicheth_sokhsedtha.service.TemplateService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/web")
public class WebController {

    private final ProfileService profileService;
    private final TemplateService templateService;
    private final PhotoStorageService photoStorageService;
    private final RegistrationNumberGenerator regNumberGenerator;

    public WebController(ProfileService profileService,
                         TemplateService templateService,
                         PhotoStorageService photoStorageService,
                         RegistrationNumberGenerator regNumberGenerator) {
        this.profileService = profileService;
        this.templateService = templateService;
        this.photoStorageService = photoStorageService;
        this.regNumberGenerator = regNumberGenerator;
    }

    // ── Profiles ──

    @GetMapping("/profiles")
    public String listProfiles(Model model) {
        List<Profile> profiles = profileService.findAll();
        model.addAttribute("profiles", profiles);
        return "profiles";
    }

    @GetMapping("/profiles/create")
    public String createProfileForm(Model model) {
        model.addAttribute("profile", new Profile());
        model.addAttribute("types", ProfileType.values());
        model.addAttribute("barcodeTypes", BarcodeType.values());
        model.addAttribute("templates", templateService.findAll());
        return "profile-form";
    }

    @PostMapping("/profiles/create")
    public String createProfile(@RequestParam String fullName,
                                @RequestParam(required = false) String title,
                                @RequestParam String type,
                                @RequestParam(required = false) String department,
                                @RequestParam(required = false) String email,
                                @RequestParam(required = false) String phone,
                                @RequestParam(required = false) String dateOfBirth,
                                @RequestParam(required = false) String bloodGroup,
                                @RequestParam(required = false) String issueDate,
                                @RequestParam(required = false) String expiryDate,
                                @RequestParam(required = false) String registrationNumber,
                                @RequestParam(required = false) String barcodeType,
                                @RequestParam(required = false) Long templateId,
                                @RequestParam(required = false) MultipartFile photo,
                                RedirectAttributes redirectAttributes) throws IOException {

        Profile profile = new Profile();
        profile.setFullName(fullName);
        profile.setTitle(title);
        profile.setType(ProfileType.valueOf(type));
        profile.setDepartment(department);
        profile.setEmail(email);
        profile.setPhone(phone);
        if (dateOfBirth != null && !dateOfBirth.isEmpty())
            profile.setDateOfBirth(LocalDate.parse(dateOfBirth));
        profile.setBloodGroup(bloodGroup);
        if (issueDate != null && !issueDate.isEmpty())
            profile.setIssueDate(LocalDate.parse(issueDate));
        if (expiryDate != null && !expiryDate.isEmpty())
            profile.setExpiryDate(LocalDate.parse(expiryDate));

        // Auto-generate registration number if not provided
        if (registrationNumber == null || registrationNumber.isBlank()) {
            String deptCode = department != null && department.length() >= 3 ? department.substring(0, 3).toUpperCase() : "GEN";
            profile.setRegistrationNumber(regNumberGenerator.next(deptCode));
        } else {
            profile.setRegistrationNumber(registrationNumber);
        }

        if (barcodeType != null && !barcodeType.isEmpty()) {
            profile.setBarcodeType(BarcodeType.valueOf(barcodeType));
        }

        if (templateId != null) {
            templateService.findById(templateId).ifPresent(profile::setTemplate);
        }

        Profile saved = profileService.save(profile);

        // Handle photo upload
        if (photo != null && !photo.isEmpty()) {
            String fileName = photoStorageService.store(saved.getUuid(), photo);
            saved.setPhotoFileName(fileName);
            saved.setPhotoContentType(photo.getContentType());
            profileService.save(saved);
        }

        redirectAttributes.addFlashAttribute("success", "Profile created successfully!");
        return "redirect:/web/profiles";
    }

    @GetMapping("/profiles/edit/{id}")
    public String editProfileForm(@PathVariable Long id, Model model) {
        Optional<Profile> opt = profileService.findById(id);
        if (opt.isEmpty()) {
            return "redirect:/web/profiles";
        }
        model.addAttribute("profile", opt.get());
        model.addAttribute("types", ProfileType.values());
        model.addAttribute("barcodeTypes", BarcodeType.values());
        model.addAttribute("templates", templateService.findAll());
        return "profile-form";
    }

    @PostMapping("/profiles/edit/{id}")
    public String updateProfile(@PathVariable Long id,
                                @RequestParam String fullName,
                                @RequestParam(required = false) String title,
                                @RequestParam String type,
                                @RequestParam(required = false) String department,
                                @RequestParam(required = false) String email,
                                @RequestParam(required = false) String phone,
                                @RequestParam(required = false) String dateOfBirth,
                                @RequestParam(required = false) String bloodGroup,
                                @RequestParam(required = false) String issueDate,
                                @RequestParam(required = false) String expiryDate,
                                @RequestParam(required = false) String registrationNumber,
                                @RequestParam(required = false) String barcodeType,
                                @RequestParam(required = false) Long templateId,
                                @RequestParam(required = false) MultipartFile photo,
                                RedirectAttributes redirectAttributes) throws IOException {

        Optional<Profile> opt = profileService.findById(id);
        if (opt.isEmpty()) {
            return "redirect:/web/profiles";
        }

        Profile profile = opt.get();
        profile.setFullName(fullName);
        profile.setTitle(title);
        profile.setType(ProfileType.valueOf(type));
        profile.setDepartment(department);
        profile.setEmail(email);
        profile.setPhone(phone);
        if (dateOfBirth != null && !dateOfBirth.isEmpty())
            profile.setDateOfBirth(LocalDate.parse(dateOfBirth));
        profile.setBloodGroup(bloodGroup);
        if (issueDate != null && !issueDate.isEmpty())
            profile.setIssueDate(LocalDate.parse(issueDate));
        if (expiryDate != null && !expiryDate.isEmpty())
            profile.setExpiryDate(LocalDate.parse(expiryDate));

        if (registrationNumber != null && !registrationNumber.isBlank()) {
            profile.setRegistrationNumber(registrationNumber);
        }

        if (barcodeType != null && !barcodeType.isEmpty()) {
            profile.setBarcodeType(BarcodeType.valueOf(barcodeType));
        }

        if (templateId != null) {
            templateService.findById(templateId).ifPresent(profile::setTemplate);
        } else {
            profile.setTemplate(null);
        }

        // Handle photo upload
        if (photo != null && !photo.isEmpty()) {
            String fileName = photoStorageService.store(profile.getUuid(), photo);
            profile.setPhotoFileName(fileName);
            profile.setPhotoContentType(photo.getContentType());
        }

        profileService.save(profile);
        redirectAttributes.addFlashAttribute("success", "Profile updated successfully!");
        return "redirect:/web/profiles";
    }

    @PostMapping("/profiles/delete/{id}")
    public String deleteProfile(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        profileService.delete(id);
        redirectAttributes.addFlashAttribute("success", "Profile deleted successfully!");
        return "redirect:/web/profiles";
    }

    @PostMapping("/profiles/{id}/photo")
    public String uploadPhoto(@PathVariable Long id,
                              @RequestParam MultipartFile photo,
                              RedirectAttributes redirectAttributes) throws IOException {
        Optional<Profile> opt = profileService.findById(id);
        if (opt.isPresent()) {
            Profile profile = opt.get();
            String fileName = photoStorageService.store(profile.getUuid(), photo);
            profile.setPhotoFileName(fileName);
            profile.setPhotoContentType(photo.getContentType());
            profileService.save(profile);
            redirectAttributes.addFlashAttribute("success", "Photo uploaded successfully!");
        }
        return "redirect:/web/profiles/edit/" + id;
    }

    // ── Templates ──

    @GetMapping("/templates")
    public String listTemplates(Model model) {
        List<Template> templates = templateService.findAll();
        model.addAttribute("templates", templates);
        return "templates";
    }

    @GetMapping("/templates/create")
    public String createTemplateForm(Model model) {
        model.addAttribute("template", new Template());
        return "template-form";
    }

    @PostMapping("/templates/create")
    public String createTemplate(@RequestParam String name,
                                 @RequestParam(required = false) String code,
                                 @RequestParam String organizationName,
                                 @RequestParam(required = false) String tagline,
                                 @RequestParam(required = false) String primaryColor,
                                 @RequestParam(required = false) String secondaryColor,
                                 @RequestParam(required = false) String textColor,
                                 RedirectAttributes redirectAttributes) {

        Template template = new Template();
        template.setName(name);
        template.setCode(code != null && !code.isEmpty() ? code : "DEFAULT");
        template.setOrganizationName(organizationName);
        template.setTagline(tagline);
        template.setPrimaryColor(primaryColor != null ? primaryColor : "#1d4ed8");
        template.setSecondaryColor(secondaryColor != null ? secondaryColor : "#e0e7ff");
        template.setTextColor(textColor != null ? textColor : "#ffffff");

        templateService.save(template);
        redirectAttributes.addFlashAttribute("success", "Template created successfully!");
        return "redirect:/web/templates";
    }

    @GetMapping("/templates/edit/{id}")
    public String editTemplateForm(@PathVariable Long id, Model model) {
        Optional<Template> opt = templateService.findById(id);
        if (opt.isEmpty()) {
            return "redirect:/web/templates";
        }
        model.addAttribute("template", opt.get());
        return "template-form";
    }

    @PostMapping("/templates/edit/{id}")
    public String updateTemplate(@PathVariable Long id,
                                 @RequestParam String name,
                                 @RequestParam(required = false) String code,
                                 @RequestParam String organizationName,
                                 @RequestParam(required = false) String tagline,
                                 @RequestParam(required = false) String primaryColor,
                                 @RequestParam(required = false) String secondaryColor,
                                 @RequestParam(required = false) String textColor,
                                 RedirectAttributes redirectAttributes) {

        Optional<Template> opt = templateService.findById(id);
        if (opt.isEmpty()) {
            return "redirect:/web/templates";
        }

        Template template = opt.get();
        template.setName(name);
        template.setCode(code != null && !code.isEmpty() ? code : "DEFAULT");
        template.setOrganizationName(organizationName);
        template.setTagline(tagline);
        template.setPrimaryColor(primaryColor != null ? primaryColor : "#1d4ed8");
        template.setSecondaryColor(secondaryColor != null ? secondaryColor : "#e0e7ff");
        template.setTextColor(textColor != null ? textColor : "#ffffff");

        templateService.save(template);
        redirectAttributes.addFlashAttribute("success", "Template updated successfully!");
        return "redirect:/web/templates";
    }

    @PostMapping("/templates/delete/{id}")
    public String deleteTemplate(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        templateService.delete(id);
        redirectAttributes.addFlashAttribute("success", "Template deleted successfully!");
        return "redirect:/web/templates";
    }

    // ── ID Cards ──

    @GetMapping("/id-cards")
    public String listIdCards(Model model) {
        List<Profile> profiles = profileService.findAll();
        model.addAttribute("profiles", profiles);
        return "id-cards";
    }

    @GetMapping("/id-cards/batch")
    public String batchGenerationForm(Model model) {
        List<Profile> profiles = profileService.findAll();
        model.addAttribute("profiles", profiles);
        return "batch";
    }
}