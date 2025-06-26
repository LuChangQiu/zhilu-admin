package com.zl.mjga.controller;

import com.zl.mjga.dto.library.LibraryDocUpdateDto;
import com.zl.mjga.dto.library.LibraryUpsertDto;
import com.zl.mjga.repository.LibraryDocRepository;
import com.zl.mjga.repository.LibraryRepository;
import com.zl.mjga.service.RagService;
import com.zl.mjga.service.UploadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.generated.mjga.tables.pojos.Library;
import org.jooq.generated.mjga.tables.pojos.LibraryDoc;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/library")
@RequiredArgsConstructor
@Slf4j
public class LibraryController {

  private final UploadService uploadService;
  private final RagService ragService;
  private final LibraryRepository libraryRepository;
  private final LibraryDocRepository libraryDocRepository;

  @PostMapping("/upsert")
  public void upsertLibrary(@RequestBody @Valid LibraryUpsertDto libraryUpsertDto) {
    Library library = new Library();
    library.setId(libraryUpsertDto.id());
    library.setName(libraryUpsertDto.name());
    library.setDesc(libraryUpsertDto.desc());
    libraryRepository.merge(library);
  }

  @DeleteMapping
  public void deleteLibrary(@RequestParam Long libraryId) {
    ragService.deleteLibraryBy(libraryId);
  }

  @DeleteMapping("/doc")
  public void deleteLibraryDoc(@RequestParam Long libraryDocId) {
    ragService.deleteDocBy(libraryDocId);
  }

  @PutMapping("/doc")
  public void updateLibraryDoc(@RequestBody @Valid LibraryDocUpdateDto libraryDocUpdateDto) {
    LibraryDoc libraryDoc = new LibraryDoc();
    libraryDoc.setId(libraryDocUpdateDto.id());
    libraryDoc.setEnable(libraryDocUpdateDto.enable());
    libraryDocRepository.merge(libraryDoc);
  }

  @PostMapping(
      value = "/upload",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
      produces = MediaType.TEXT_PLAIN_VALUE)
  public String uploadLibraryDoc(
      @RequestPart("libraryId") Long libraryId, @RequestPart("file") MultipartFile multipartFile)
      throws Exception {
    String objectName = uploadService.uploadLibraryDoc(multipartFile);
    Long libraryDocId =
        ragService.createLibraryDocBy(libraryId, objectName, multipartFile.getOriginalFilename());
    ragService.embeddingAndCreateDocSegment(libraryDocId, objectName);
    return objectName;
  }
}
