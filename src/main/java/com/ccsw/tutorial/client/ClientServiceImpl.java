package com.ccsw.tutorial.client;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import com.ccsw.tutorial.borrowing.BorrowingService;
import com.ccsw.tutorial.client.model.Client;
import com.ccsw.tutorial.client.model.ClientDto;

/**
 * @author KatiaN14
 *
 */
@Service
@Transactional
public class ClientServiceImpl implements ClientService {

    @Autowired
    ClientRepository clientRepository;

    @Autowired
    @Lazy
    BorrowingService borrowingService;

    /**
     * {@inheritDoc}
     */
    @Override
    public Client get (Long id) {
        return this.clientRepository.findById(id).orElse(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Client> findAll() {
        return (List<Client>) this.clientRepository.findAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void save(Long id, ClientDto dto) throws Exception {
        Client existe = this.clientRepository.findByNameIgnoreCase(dto.getName());
        if (existe != null && !existe.getId().equals(id)) {
            throw new ResponseStatusException (HttpStatus.BAD_REQUEST, "Ya existe un cliente con este nombre");
        }
        Client client;
        if (id == null) {
            client = new Client();
        } else {
            client = this.get(id);
        }

        client.setName(dto.getName());

        this.clientRepository.save(client);
    }

    /**
     * {@inheritDoc}
     */
    public void delete(Long id) throws Exception {
        if(this.get(id) == null){
            throw new Exception("Not exists");
        }

        if (this.borrowingService.existsClient(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El cliente tiene préstamos y no se puede eliminar");
        }

        this.clientRepository.deleteById(id);
    }
}
