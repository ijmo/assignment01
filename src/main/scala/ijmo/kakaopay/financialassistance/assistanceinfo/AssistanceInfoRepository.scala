package ijmo.kakaopay.financialassistance.assistanceinfo

import org.springframework.data.domain.{Page, Pageable}
import org.springframework.data.jpa.repository.{JpaRepository, Query}

trait AssistanceInfoRepository extends JpaRepository[AssistanceInfo, java.lang.Long] {
  // COALESCE
  @Query(value = "SELECT name FROM (SELECT o.name, ai.max_amount_num, (rate1 + rate2)/2.0 AS avg_rate " +
                   "FROM assistance_info ai INNER JOIN organization o ON ai.organization_code = o.code) " +
                  "ORDER BY max_amount_num DESC, avg_rate DESC"
    , countQuery = "SELECT COUNT(*) FROM assistance_info ai INNER JOIN organization o ON ai.organization_code = o.code"
    , nativeQuery = true)
  def findOrganizationNames(pageable: Pageable): Page[String]

  @Query(value = "SELECT o.name FROM (SELECT organization_code code, rate1 rate " +
                                       "FROM assistance_info ORDER BY rate1 ASC LIMIT 1) ai " +
             "INNER JOIN organization o ON ai.code = o.code"
    , nativeQuery = true)
  def findOrganizationNamesWithMinimumRate(): java.lang.Iterable[String]

  @Query(value = "SELECT organization_code code, SQRT(POWER(longitude - :x, 2) + POWER(latitude - :y, 2)) distance " +
                   "FROM assistance_info " +
                  "WHERE (longitude IS NOT NULL OR latitude IS NOT NULL) AND usages LIKE  %:usages% " +
                    "AND max_amount_num <= :maxAmount AND rate1 <= :rateLimit ORDER BY distance ASC LIMIT 1"
    , nativeQuery = true)
  def findByXAndYAndUsagesAndMaxAmountAndRateLimit(x: Double, y: Double, usages: String, maxAmount: Long, rateLimit: Double): Array[Object]

  def findById(id: Long): AssistanceInfo
  def findByOrganizationName(organizationName: String): AssistanceInfo
  def findByOrganizationCode(organizationCode: String): AssistanceInfo
}

// select management from (SELECT management, SQRT(POWER(longitude - 0.0, 2) + POWER(latitude, 2)) dist FROM ASSISTANCE_INFO where longitude is not null or latitude is not null order by dist asc limit 1);