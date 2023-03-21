package org.test.app.servicespring.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.test.app.servicespring.irepositories.IBancoRepository;
import org.test.app.servicespring.irepositories.ICuentaRepository;
import org.test.app.servicespring.iservices.ICuentaService;
import org.test.app.servicespring.models.Banco;
import org.test.app.servicespring.models.Cuenta;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CuentaServiceImpl implements ICuentaService {

    private ICuentaRepository cuentaRepository;
    private IBancoRepository bancoRepository;

    public CuentaServiceImpl(ICuentaRepository cuentaRepository, IBancoRepository bancoRepository) {
        this.cuentaRepository = cuentaRepository;
        this.bancoRepository = bancoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Cuenta findById(Long id) {
        return cuentaRepository.findById(id).orElseThrow();
    }

    @Override
    @Transactional(readOnly = true)
    public int getTotalTransferences(Long bancoId) {
        return bancoRepository.findById(bancoId).orElseThrow().getTotalTransferencias();
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal reviewSaldo(Long cuentaId) {
        return cuentaRepository.findById(cuentaId).orElseThrow().getSaldo();
    }

    @Override
    @Transactional
    public void transfer(Long numCuentaOrigin, Long numCuentaFinal, BigDecimal monto, Long bancoId) {
        Cuenta cOrigin = cuentaRepository.findById(numCuentaOrigin).orElseThrow();
        cOrigin.debito(monto);
        cuentaRepository.save(cOrigin);

        Cuenta cFinal = cuentaRepository.findById(numCuentaFinal).orElseThrow();
        cFinal.credito(monto);
        cuentaRepository.save(cFinal);

        Banco banco = bancoRepository.findById(bancoId).orElseThrow();
        int totalTransferences = banco.getTotalTransferencias();
        banco.setTotalTransferencias(++totalTransferences);
        bancoRepository.save(banco);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cuenta> findAll() {
        return cuentaRepository.findAll();
    }

    @Override
    @Transactional
    public Cuenta save(Cuenta cuenta) {
        return cuentaRepository.save(cuenta);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        cuentaRepository.deleteById(id);
    }
}
