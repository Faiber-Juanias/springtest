package org.test.app.servicespring.services;

import org.springframework.stereotype.Service;
import org.test.app.servicespring.irepositories.IBancoRepository;
import org.test.app.servicespring.irepositories.ICuentaRepository;
import org.test.app.servicespring.iservices.ICuentaService;
import org.test.app.servicespring.models.Banco;
import org.test.app.servicespring.models.Cuenta;

import java.math.BigDecimal;

@Service
public class CuentaServiceImpl implements ICuentaService {

    private ICuentaRepository cuentaRepository;
    private IBancoRepository bancoRepository;

    public CuentaServiceImpl(ICuentaRepository cuentaRepository, IBancoRepository bancoRepository) {
        this.cuentaRepository = cuentaRepository;
        this.bancoRepository = bancoRepository;
    }

    @Override
    public Cuenta findById(Long id) {
        return cuentaRepository.findById(id);
    }

    @Override
    public int getTotalTransferences(Long bancoId) {
        return bancoRepository.findById(bancoId).getTotalTransferencias();
    }

    @Override
    public BigDecimal reviewSaldo(Long cuentaId) {
        return cuentaRepository.findById(cuentaId).getSaldo();
    }

    @Override
    public void transfer(Long numCuentaOrigin, Long numCuentaFinal, BigDecimal monto, Long bancoId) {
        Cuenta cOrigin = cuentaRepository.findById(numCuentaOrigin);
        cOrigin.debito(monto);
        cuentaRepository.update(cOrigin);

        Cuenta cFinal = cuentaRepository.findById(numCuentaFinal);
        cFinal.credito(monto);
        cuentaRepository.update(cFinal);

        Banco banco = bancoRepository.findById(bancoId);
        int totalTransferences = banco.getTotalTransferencias();
        banco.setTotalTransferencias(++totalTransferences);
        bancoRepository.update(banco);
    }
}
