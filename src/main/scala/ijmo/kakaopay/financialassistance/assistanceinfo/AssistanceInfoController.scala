package ijmo.kakaopay.financialassistance.assistanceinfo

import java.net.URI

import ijmo.kakaopay.financialassistance.search.SearchQueryDTO
import javax.validation.Valid
import org.springframework.data.domain.{PageRequest, Pageable}
import org.springframework.http.{HttpHeaders, HttpStatus, ResponseEntity}
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation._

import scala.collection.JavaConverters._

@RestController
@RequestMapping(Array("/api/assistanceinfo"))
class AssistanceInfoController (val assistanceInfoService: AssistanceInfoService) {
  /**
    * (필수) 레코드 저장
    */
  @PreAuthorize("#oauth2.hasScope('write') and #oauth2.hasScope('assistanceInfo')")
  @PostMapping(Array(""))
  def create(@Valid @RequestBody assistanceInfoDTO: AssistanceInfoDTO, result: BindingResult): ResponseEntity[Object] = {
    if (result.hasErrors) {
      val msg = result.getAllErrors.asScala.map(_.getDefaultMessage).mkString(", ")
      return new ResponseEntity(msg, HttpStatus.BAD_REQUEST)
    }
    val assistanceInfo = assistanceInfoService.addAssistanceInfo(assistanceInfoDTO)
    val headers: HttpHeaders = new HttpHeaders()
    headers.setLocation(URI.create("/api/assistanceinfo/" + assistanceInfo.getId))
    new ResponseEntity(HttpStatus.CREATED)
  }

  /**
    * (필수) 목록 검색
    */
  @PreAuthorize("#oauth2.hasScope('read') and #oauth2.hasScope('assistanceInfo')")
  @GetMapping(Array(""))
  def list(): java.lang.Iterable[AssistanceInfoDTO] = {
    val infos = assistanceInfoService.findAll
    infos.map(AssistanceInfoDTO(_)).asJava
  }

  /**
    * (필수) 특정 지자체명의 지원정보 검색
    */
  @PreAuthorize("#oauth2.hasScope('read') and #oauth2.hasScope('assistanceInfo')")
  @PostMapping(Array("/match"))
  def get(@RequestBody assistanceInfoDTO: AssistanceInfoDTO): ResponseEntity[Object] = {
    if (assistanceInfoDTO.getRegion == null || assistanceInfoDTO.getRegion.trim == "") {
      return new ResponseEntity(HttpStatus.BAD_REQUEST)
    }
    val result = assistanceInfoService.findByOrganizationName(assistanceInfoDTO.getRegion)
    if (result == null) return new ResponseEntity(HttpStatus.NOT_FOUND)
    new ResponseEntity(AssistanceInfoDTO(result), HttpStatus.OK)
  }

  /**
    * (필수) 지자체 지원정보 수정
    */
  @PreAuthorize("#oauth2.hasScope('write') and #oauth2.hasScope('assistanceInfo')")
  @PutMapping(Array("/{id}"))
  def update(@PathVariable id: Long, @Valid @RequestBody assistanceInfoDTO: AssistanceInfoDTO, result: BindingResult): ResponseEntity[Object] = {
    if (result.hasErrors) {
      val msg = result.getAllErrors.asScala.map(_.getDefaultMessage).mkString(", ")
      return new ResponseEntity(msg, HttpStatus.BAD_REQUEST)
    }
    val assistanceInfo = assistanceInfoService.findById(id).orNull
    if (assistanceInfo == null) return new ResponseEntity(HttpStatus.NOT_FOUND)

    assistanceInfoService.updateAssistanceInfo(assistanceInfo, assistanceInfoDTO)
    val headers: HttpHeaders = new HttpHeaders()
    headers.setLocation(URI.create("/api/assistanceinfo/" + assistanceInfo.getId))
    new ResponseEntity(HttpStatus.OK)
  }

  /**
    * (필수) 지원금액 내림차순하여 특정개수만 출력
    */
  @PreAuthorize("#oauth2.hasScope('read') and #oauth2.hasScope('assistanceInfo')")
  @GetMapping(Array("/find"))
  def list(@RequestParam limit: Int): java.lang.Iterable[String] = {
    val pageNum = 0
    val pageable: Pageable = PageRequest.of(pageNum, limit)
    assistanceInfoService.findOrganizationNames(pageable).asJava
  }

  /**
    * (필수) 보전 비율이 가장 작은 지원정보의 기관명
    */
  @PreAuthorize("#oauth2.hasScope('read') and #oauth2.hasScope('assistanceInfo')")
  @GetMapping(Array("/minimumRate"))
  def listMinimumRate(): java.lang.Iterable[String] = {
    assistanceInfoService.findOrganizationNamesWithMinimumRate.asJava
  }

  /**
    * (선택) 텍스트 분석해서 추천
    */
  @PreAuthorize("#oauth2.hasScope('read') and #oauth2.hasScope('assistanceInfo')")
  @PostMapping(Array("/search"))
  def search(@Valid @RequestBody query: SearchQueryDTO, result: BindingResult): ResponseEntity[Object] = {
    if (result.hasErrors) {
      val msg = result.getAllErrors.asScala.map(_.getDefaultMessage).mkString(", ")
      return new ResponseEntity(msg, HttpStatus.BAD_REQUEST)
    }
    val recommended = assistanceInfoService.searchByText(query.getInput)
    if (recommended.isEmpty) new ResponseEntity(recommended, HttpStatus.NOT_FOUND)
    new ResponseEntity(recommended.get.asJava, HttpStatus.OK)
  }
}
