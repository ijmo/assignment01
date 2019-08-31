package ijmo.kakaopay.financialassistance.assistanceinfo

import org.springframework.data.domain.{Page, Pageable}
import org.springframework.data.jpa.repository.{EntityGraph, JpaRepository, Query}

trait AssistanceInfoRepository extends JpaRepository[AssistanceInfo, java.lang.Long] {

  @EntityGraph(attributePaths = Array("organization", "recommenders"))
  override def findAll(): java.util.List[AssistanceInfo]

  @EntityGraph(attributePaths = Array("organization", "recommenders"))
  override def findById(id: java.lang.Long): java.util.Optional[AssistanceInfo]

  @EntityGraph(attributePaths = Array("organization", "recommenders"))
  def findByOrganizationName(organizationName: String): AssistanceInfo

  @EntityGraph(attributePaths = Array("organization", "recommenders"))
  def findByOrganizationCode(organizationCode: String): AssistanceInfo

  @Query(value = "SELECT name " +
                   "FROM (SELECT o.name, ai.max_amount_num, (rate1 + rate2)/2.0 AS avg_rate " +
                           "FROM assistance_info ai INNER JOIN organization o ON ai.organization_code = o.code) " +
                  "ORDER BY (CASE WHEN max_amount_num >= 9000000000000000000 THEN 0 ELSE max_amount_num END) DESC, avg_rate ASC"
    , countQuery = "SELECT COUNT(*) FROM assistance_info ai INNER JOIN organization o ON ai.organization_code = o.code"
    , nativeQuery = true)
  def findOrganizationNamesOrderByMaxAmountNumDescAvgRateAsc(pageable: Pageable): Page[String]

  @Query(value = "SELECT o.name FROM (SELECT organization_code code, rate1 rate " +
                                       "FROM assistance_info ORDER BY rate1 ASC LIMIT 1) ai " +
             "INNER JOIN organization o ON ai.code = o.code"
    , nativeQuery = true)
  def findOrganizationNamesWithMinimumRate: java.lang.Iterable[String]

  @Query(value = "SELECT organization_code code, SQRT(POWER(longitude - :x, 2) + POWER(latitude - :y, 2)) distance " +
                   "FROM assistance_info " +
                  "WHERE (longitude IS NOT NULL OR latitude IS NOT NULL) " +
                    "AND usages LIKE %:usages% " +
                    "AND max_amount_num >= :maxAmount " +
                    "AND rate2 >= :rateLimit " +
                 " ORDER BY distance ASC LIMIT 1"
    , nativeQuery = true)
  def findByXAndYAndUsagesAndMaxAmountAndRateLimit(x: Double, y: Double, usages: String, maxAmount: Long, rateLimit: Double): Array[Object]
}
