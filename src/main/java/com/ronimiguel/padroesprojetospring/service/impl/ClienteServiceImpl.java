package com.ronimiguel.padroesprojetospring.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ronimiguel.padroesprojetospring.model.Cliente;
import com.ronimiguel.padroesprojetospring.model.ClienteRepository;
import com.ronimiguel.padroesprojetospring.model.Endereco;
import com.ronimiguel.padroesprojetospring.model.EnderecoRepository;
import com.ronimiguel.padroesprojetospring.service.ClienteService;
import com.ronimiguel.padroesprojetospring.service.ViaCepService;

@Service
public class ClienteServiceImpl implements ClienteService {

	// Singleton -Injeta os componentes com @Autowired
	@Autowired
	private ClienteRepository clienteRepository;
	@Autowired
	private EnderecoRepository enderecoRepository;
	@Autowired
	private ViaCepService viaCepService;

	// Strategy -Implementa os metodos da interface
	// Facade -Abstrai integracao com uma interface simples

	@Override
	public Iterable<Cliente> buscarTodos() {
		// Consulta Clientes
		return clienteRepository.findAll();
	}

	@Override
	public Cliente buscarPorId(Long id) {
		// Consulta Cliente pelo ID
		Optional<Cliente> cliente = clienteRepository.findById(id);
		return cliente.get();
	}

	@Override
	public void inserir(Cliente cliente) {
		salvarClienteComCep(cliente);
	}

	@Override
	public void atualizar(Long id, Cliente cliente) {
		Optional<Cliente> clienteBd = clienteRepository.findById(id);
		if (clienteBd.isPresent()) {
			salvarClienteComCep(cliente);
		}
	}

	@Override
	public void deletar(Long id) {
		// Deleta Cliente pelo ID
		clienteRepository.deleteById(id);
	}

	private void salvarClienteComCep(Cliente cliente) {
		// Verifica Endereco existe atravez do CEP
		String cep = cliente.getEndereco().getCep();
		Endereco endereco = enderecoRepository.findById(cep).orElseGet(() -> {
			// integrar com o ViaCEP e persistir o retorno
			Endereco novoEndereco = viaCepService.consultarCep(cep);
			enderecoRepository.save(novoEndereco);
			return novoEndereco;
		});
		cliente.setEndereco(endereco);
		// Salva dados do Cliente
		clienteRepository.save(cliente);
	}

}
