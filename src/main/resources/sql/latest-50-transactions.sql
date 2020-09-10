select t.timestamp,
       t.caller,
       t.status,
       case t.kind
           when 1 then 'construct'
           when 2 then 'approveAllowance'
           when 3 then 'mint'
           when 4 then 'burn'
           when 5 then 'transfer'
           when 6 then 'transferFrom'
           when 7 then 'proposeOwner'
           when 8 then 'claimOwnership'
           when 9 then 'changeSupplyManager'
           when 10 then 'changeComplianceManager'
           when 11 then 'freeze'
           when 12 then 'unfreeze'
           when 13 then 'wipe'
           when 14 then 'setKycPassed'
           when 15 then 'unsetKycPassed'
           when 16 then 'increaseAllowance'
           when 17 then 'decreaseAllowance'
           when 18 then 'changeEnforcementManager'
           when 19 then 'approveExternalTransfer'
           when 20 then 'externalTransfer'
           when 21 then 'externalTransferFrom'
           end as transaction,
       case t.kind
           when 1 then (
               select jsonb_build_object(
                              'tokenName', tc.token_name,
                              'tokenSymbol', tc.token_symbol,
                              'totalSupply', '' || tc.total_supply,
                              'supplyManager', encode(tc.supply_manager, 'hex'),
                              'complianceManager', encode(tc.compliance_manager, 'hex'),
                              'enforcementManager', encode(tc.enforcement_manager, 'hex'),
                              'tokenDecimal', tc.token_decimal
                          )
               from transaction_construct tc
               where tc.timestamp = t.timestamp
           )
           when 2 then (
               select jsonb_build_object('spender', encode(taa.spender, 'hex'), 'value', '' || taa.value)
               from transaction_approve_allowance taa
               where taa.timestamp = t.timestamp
           )
           when 3 then (
               select jsonb_build_object('value', '' || tm.value)
               from transaction_mint tm
               where tm.timestamp = t.timestamp
           )
           when 4 then (
               select jsonb_build_object('value', '' || tb.value)
               from transaction_burn tb
               where tb.timestamp = t.timestamp
           )
           when 5 then (
               select jsonb_build_object('to', encode(tt.receiver, 'hex'), 'value', '' || tt.value)
               from transaction_transfer tt
               where tt.timestamp = t.timestamp
           )
           when 6 then (
               select jsonb_build_object('from', encode(ttf.sender, 'hex'), 'to', encode(ttf.receiver, 'hex'), 'value',
                                         '' || ttf.value)
               from transaction_transfer_from ttf
               where ttf.timestamp = t.timestamp
           )
           when 7 then (
               select jsonb_build_object('address', encode(tpo.address, 'hex'))
               from transaction_propose_owner tpo
               where tpo.timestamp = t.timestamp
           )
           when 8 then (SELECT jsonb_build_object())
           when 9 then (
               select jsonb_build_object('address', encode(tcsm.address, 'hex'))
               from transaction_change_supply_manager tcsm
               where tcsm.timestamp = t.timestamp
           )
           when 10 then (
               select jsonb_build_object('address', encode(tccm.address, 'hex'))
               from transaction_change_compliance_manager tccm
               where tccm.timestamp = t.timestamp
           )
           when 11 then (
               select jsonb_build_object('address', encode(tf.address, 'hex'))
               from transaction_freeze tf
               where tf.timestamp = t.timestamp
           )
           when 12 then (
               select jsonb_build_object('address', encode(tu.address, 'hex'))
               from transaction_unfreeze tu
               where tu.timestamp = t.timestamp
           )
           when 13 then (
               select jsonb_build_object('address', encode(tw.address, 'hex'), 'value', '' || tw.value)
               from transaction_wipe tw
               where tw.timestamp = t.timestamp
           )
           when 14 then (
               select jsonb_build_object('address', encode(tskp.address, 'hex'))
               from transaction_set_kyc_passed tskp
               where tskp.timestamp = t.timestamp
           )
           when 15 then (
               select jsonb_build_object('address', encode(tukp.address, 'hex'))
               from transaction_unset_kyc_passed tukp
               where tukp.timestamp = t.timestamp
           )
           when 16 then (
               select jsonb_build_object('spender', encode(tia.spender, 'hex'), 'value', '' || tia.value)
               from transaction_increase_allowance tia
               where tia.timestamp = t.timestamp
           )
           when 17 then (
               select jsonb_build_object('spender', encode(tda.spender, 'hex'), 'value', '' || tda.value)
               from transaction_decrease_allowance tda
               where tda.timestamp = t.timestamp
           )
           when 18 then (
               select jsonb_build_object('address', encode(tcem.address, 'hex'))
               from transaction_change_enforcement_manager tcem
               where tcem.timestamp = t.timestamp
           )
           when 19 then (
               select jsonb_build_object(
                              'network', taet.network_uri,
                              'to', convert_from(taet.address_to, 'UTF-8'),
                              'value', '' || taet.amount
                          )
               from transaction_approve_external_transfer taet
               where taet.timestamp = t.timestamp
           )
           when 20 then (
               select jsonb_build_object(
                              'network', tet.network_uri,
                              'from', encode(tet.address_from, 'hex'),
                              'to', convert_from(tet.address_to, 'UTF-8'),
                              'value', '' || tet.amount
                          )
               from transaction_external_transfer tet
               where tet.timestamp = t.timestamp
           )
           when 21 then (
               select jsonb_build_object(
                              'network', tet.network_uri,
                              'to', encode(tet.address_to, 'hex'),
                              'from', convert_from(tet.address_from, 'UTF-8'),
                              'value', '' || tet.amount
                          )
               from transaction_external_transfer_from tet
               where tet.timestamp = t.timestamp
           )
           end as data
from transaction t
order by t.timestamp desc
limit 50
