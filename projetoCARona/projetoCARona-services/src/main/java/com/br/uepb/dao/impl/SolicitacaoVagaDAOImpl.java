package com.br.uepb.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;

import com.br.uepb.constants.MensagensErro;
import com.br.uepb.dao.SolicitacaoVagaDAO;
import com.br.uepb.dao.hibernateUtil.HibernateUtil;
import com.br.uepb.domain.CaronaDomain;
import com.br.uepb.domain.SolicitacaoVagaDomain;
import com.br.uepb.exceptions.ProjetoCaronaException;

public class SolicitacaoVagaDAOImpl implements SolicitacaoVagaDAO {
	
	final static Logger logger = Logger.getLogger(SolicitacaoVagaDAOImpl.class);
	
	//Funcoes para a base de dados
	private SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
	private Session session;
	private Transaction transaction;
	private Criteria criteria;
	private Disjunction disjunction;
	
	
	private static SolicitacaoVagaDAOImpl solocitacaoVagaDAOImpl;
	
	public static SolicitacaoVagaDAOImpl getInstance(){
		if(solocitacaoVagaDAOImpl == null){
			solocitacaoVagaDAOImpl = new SolicitacaoVagaDAOImpl();
			return solocitacaoVagaDAOImpl;
		}else{
			return solocitacaoVagaDAOImpl;
		}
	}
	
	public int getIdSolicitacao() throws Exception{
		int idSolicitacao;
		String obj;
		try{
		session = sessionFactory.openSession();	
		transaction = session.beginTransaction();
		obj =  (String)session.createQuery("Select Max(id) from SolicitacaoVagaDomain").uniqueResult();
		transaction.commit();
		session.close();
		}catch(Exception e){
			System.out.println(e.getMessage());
			throw e;
		}
		
		if(obj == null){
			idSolicitacao = 1;
		}else{
			idSolicitacao = Integer.parseInt(obj);
			idSolicitacao++;
		}
		return idSolicitacao;
	}
	
	@Override
	public void addSolicitacaoVaga(SolicitacaoVagaDomain solicitacaoVaga) throws Exception {
		try{
			session = sessionFactory.openSession();	
			transaction = session.beginTransaction();
			session.save(solicitacaoVaga);
			transaction.commit();
			session.close();
		}catch(Exception e){
			System.out.println(e.getMessage());
			throw e;
		}
	}
	
	
	@Override
	public void deleteSolicitacaoVaga(String idSolicitacao) throws Exception{		
		SolicitacaoVagaDomain solicitacaoVaga = getSolicitacaoVaga(idSolicitacao);
		session = sessionFactory.openSession();	
		transaction = session.beginTransaction();
		session.delete(solicitacaoVaga);
		transaction.commit();
		session.close();
	}
	
	@Override
	public SolicitacaoVagaDomain getSolicitacaoVaga(String idSolicitacao) throws Exception {	
		SolicitacaoVagaDomain solicitacaoVagaDomain;
		
		try{
			session = sessionFactory.openSession();
			criteria = session.createCriteria(SolicitacaoVagaDomain.class);
			criteria.add(Restrictions.eq("id", idSolicitacao));
			solicitacaoVagaDomain = (SolicitacaoVagaDomain) criteria.uniqueResult();
			session.close();
		}catch(Exception e){
			System.out.println(e.getMessage());
			throw e;
		}
		
		if(solicitacaoVagaDomain != null){
			return solicitacaoVagaDomain;
		}else{
			logger.debug("getSolicitacaoVaga() Exceção: "+MensagensErro.SOLICITACAO_INEXISTENTE);
			throw new ProjetoCaronaException(MensagensErro.SOLICITACAO_INEXISTENTE);
		}
		
	}
		
	@Override
	public ArrayList<SolicitacaoVagaDomain> getSolicitacoesConfirmadas(String idCarona) throws Exception{
		ArrayList<SolicitacaoVagaDomain> solicitacoesCarona = new ArrayList<SolicitacaoVagaDomain>();
		
		try{
			session = sessionFactory.openSession();
			criteria = session.createCriteria(SolicitacaoVagaDomain.class);
			criteria.add(Restrictions.eq("idCarona", idCarona));
			criteria.add(Restrictions.eq("foiAceita", true));
			solicitacoesCarona = (ArrayList<SolicitacaoVagaDomain>)criteria.list();
			session.close();
			return solicitacoesCarona;
		}catch(Exception e){
			throw e;
		}
		
	}
	
	@Override
	public ArrayList<SolicitacaoVagaDomain> getSolicitacoesPendentes(String idCarona) throws Exception{
		ArrayList<SolicitacaoVagaDomain> solicitacoesCarona = new ArrayList<SolicitacaoVagaDomain>();
		
		try{
			session = sessionFactory.openSession();
			criteria = session.createCriteria(SolicitacaoVagaDomain.class);
			criteria.add(Restrictions.eq("idCarona", idCarona));
			criteria.add(Restrictions.eq("foiAceita", false));
			solicitacoesCarona = (ArrayList<SolicitacaoVagaDomain>)criteria.list();
			session.close();
			return solicitacoesCarona;
		}catch(Exception e){
			throw e;
		}		
	}
	
	@Override
	public List<SolicitacaoVagaDomain> getSolicitacoesPendentesPorUsuario(String login) throws Exception{
		List<SolicitacaoVagaDomain> solicitacoes = null;
		
		ArrayList<CaronaDomain> caronas = (ArrayList<CaronaDomain>) CaronaDAOImpl.getInstance().getHistoricoDeCaronas(login);
		if (caronas.size() > 0) {
			String filtro = "";
			for (CaronaDomain caronaDomain : caronas) {
				filtro += "'"+caronaDomain.getId() +"',";
			}
			//tratamento para retirar a última ", "
			if (filtro.length() > 1) {
				filtro = filtro.substring (0, filtro.length() - 1);
			}
			
			try{
				session = sessionFactory.openSession();	
				transaction = session.beginTransaction();
				solicitacoes = session.createQuery(" from SolicitacaoVagaDomain " +
						  						   " where idCarona in ("+filtro+") " +
						  						   " and foiAceita = 'false' " +
												   " order by id desc").list();
				transaction.commit();
				session.close();
			}catch(Exception e){
				throw e;
			}
		}
		
		return solicitacoes;				
	}
	
	@Override
	public SolicitacaoVagaDomain getSolicitacaoUsuario(String login, String idCarona) throws Exception{
		SolicitacaoVagaDomain solicitacaoVaga = new SolicitacaoVagaDomain();
		
		try{
			session = sessionFactory.openSession();
			criteria = session.createCriteria(SolicitacaoVagaDomain.class);
			criteria.add(Restrictions.eq("idCarona", idCarona));
			criteria.add(Restrictions.eq("idUsuario", login));
			solicitacaoVaga = (SolicitacaoVagaDomain)criteria.uniqueResult();
			session.close();
			return solicitacaoVaga;
		}catch(Exception e){
			throw e;
		}
	}
	
	@Override
	public void atualizaSolicitacaoVaga(SolicitacaoVagaDomain solicitacaoVaga){
		try 
		{
			session = sessionFactory.openSession();
			session.beginTransaction();
			session.update(solicitacaoVaga);
		    session.getTransaction().commit();
		}
		catch (Exception e) 
		{
		    e.printStackTrace();
		}

		session.close();
	}
	
	@Override
	public List<SolicitacaoVagaDomain> getHistoricoDeVagasEmCaronas(String login) throws Exception {
		List<SolicitacaoVagaDomain> solicitacoesCarona = new ArrayList<SolicitacaoVagaDomain>();
		
		try{
			session = sessionFactory.openSession();
			criteria = session.createCriteria(SolicitacaoVagaDomain.class);
			criteria.add(Restrictions.eq("idUsuario", login));
			criteria.add(Restrictions.eq("foiAceita", true));
			solicitacoesCarona = (ArrayList<SolicitacaoVagaDomain>)criteria.list();
			session.close();
			return solicitacoesCarona;
		}catch(Exception e){
			throw e;
		}		
	}
	
	@Override
	public boolean participouCarona(String idCarona, String login) throws Exception{
		try{
			session = sessionFactory.openSession();
			criteria = session.createCriteria(SolicitacaoVagaDomain.class);
			criteria.add(Restrictions.eq("idCarona", idCarona));
			criteria.add(Restrictions.eq("idUsuario", login));
			criteria.add(Restrictions.eq("foiAceita", true));
			SolicitacaoVagaDomain solicitacaoVaga = (SolicitacaoVagaDomain)criteria.uniqueResult();
			session.close();
			
			if (solicitacaoVaga == null) {
				return false;
			}
			else {
				return true;
			}
			
		}catch(Exception e){
			throw e;
		}
	}
	
	@Override
	public SolicitacaoVagaDomain getSolicitacaoVaga(String idCarona, String loginCaroneiro) throws Exception {
		try{
			session = sessionFactory.openSession();	
			transaction = session.beginTransaction();
			
			criteria = session.createCriteria(SolicitacaoVagaDomain.class);
			criteria.add(Restrictions.eq("idUsuario", loginCaroneiro));			
			criteria.add(Restrictions.eq("idCarona", idCarona));			
			criteria.add(Restrictions.eq("foiAceita", true));
			SolicitacaoVagaDomain solicitacoesVaga = (SolicitacaoVagaDomain)criteria.uniqueResult();			
			session.close();
			return solicitacoesVaga;
		}catch(Exception e){
			System.out.println(e.getMessage());
			throw e;
		}
	}
	
	@Override
	public List<SolicitacaoVagaDomain> getSolicitacoesByReviewVaga(String login, String review) throws Exception{
		List<SolicitacaoVagaDomain> solicitacaoVaga = new ArrayList<SolicitacaoVagaDomain>();
		
		try{
			session = sessionFactory.openSession();
			criteria = session.createCriteria(SolicitacaoVagaDomain.class);
			criteria.add(Restrictions.eq("idUsuario", login));
			criteria.add(Restrictions.eq("reviewVaga", review));			
			solicitacaoVaga = (ArrayList<SolicitacaoVagaDomain>)criteria.list();
			session.close();
			return solicitacaoVaga;
		}catch(Exception e){
			throw e;
		}		
	}
	
	@Override
	public List<SolicitacaoVagaDomain> getSolicitacoesByReviewCarona(List<CaronaDomain> caronas, String reviewCarona) throws Exception{
		List<SolicitacaoVagaDomain> solicitacaoVaga = new ArrayList<SolicitacaoVagaDomain>();		
		try{
			if (caronas.size() > 0) {
				session = sessionFactory.openSession();
				criteria = session.createCriteria(SolicitacaoVagaDomain.class);			
				criteria.add(Restrictions.eq("reviewCarona", reviewCarona));	
				
				//usando a disjunction para fazer um 'ou' entre vários elementos
				disjunction = Restrictions.disjunction();
				for (CaronaDomain carona : caronas) {
					disjunction.add(Restrictions.eq("idCarona", carona.getId()));
				}
				criteria.add(disjunction);
				  
				solicitacaoVaga = (ArrayList<SolicitacaoVagaDomain>)criteria.list();
				session.close();
			}
			return solicitacaoVaga;
		}catch(Exception e){
			throw e;
		}		
	}
	
	@Override
	public void apagaSolicitacoes(){
		logger.debug("apagando lista de solicitacoes");
		session = sessionFactory.openSession();	
		transaction = session.beginTransaction();
		session.createQuery("delete from SolicitacaoVagaDomain").executeUpdate();
		transaction.commit();
		session.close();
	}
}
