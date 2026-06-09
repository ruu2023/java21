WITH
dev_candidate AS (
    SELECT DISTINCT m.result, m.code
    FROM code_master m
    WHERE m.code LIKE 'DEV%'
      AND m.code IN (
        /*[# th:each="item, stat : ${inputList}"]*/
        /*[(${item})]*//*[# th:if="${!stat.last}"]*/, /*[/]*/
        /*[/]*/
      )
),
other_candidate AS (
    SELECT DISTINCT m.result, m.code
    FROM code_master m
    WHERE m.code NOT LIKE 'DEV%'
      AND m.code IN (
        /*[# th:each="item, stat : ${inputList}"]*/
        /*[(${item})]*//*[# th:if="${!stat.last}"]*/, /*[/]*/
        /*[/]*/
      )
),
dev_results AS (
    SELECT DISTINCT result
    FROM code_master
    WHERE code LIKE 'DEV%'
)
SELECT DISTINCT t.result, t.code FROM (

    -- 両方あり：積集合
    SELECT d.result, d.code
    FROM dev_candidate d
    JOIN other_candidate o ON d.result = o.result
    WHERE EXISTS (SELECT 1 FROM dev_candidate)
      AND EXISTS (SELECT 1 FROM other_candidate)

    UNION ALL

    -- 両方あり：積集合が空 → dev_candidateそのまま
    SELECT result, code FROM dev_candidate
    WHERE EXISTS (SELECT 1 FROM dev_candidate)
      AND EXISTS (SELECT 1 FROM other_candidate)
      AND NOT EXISTS (
          SELECT 1 FROM dev_candidate d JOIN other_candidate o ON d.result = o.result
      )

    UNION ALL

    -- DEVのみ
    SELECT result, code FROM dev_candidate
    WHERE EXISTS (SELECT 1 FROM dev_candidate)
      AND NOT EXISTS (SELECT 1 FROM other_candidate)

    UNION ALL

    -- OTHERのみ：DEV持ちを除外
    SELECT result, code FROM other_candidate
    WHERE NOT EXISTS (SELECT 1 FROM dev_candidate)
      AND EXISTS (SELECT 1 FROM other_candidate)
      AND result NOT IN (SELECT result FROM dev_results)

    UNION ALL

    -- 両方なし：全件
    SELECT DISTINCT result, code FROM code_master
    WHERE NOT EXISTS (SELECT 1 FROM dev_candidate)
      AND NOT EXISTS (SELECT 1 FROM other_candidate)

) t