package com.batchBoaventuraMarketplace.Batch.config;

import com.batchBoaventuraMarketplace.Batch.entity.TransacaoDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class BatchConfig {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Bean
    @StepScope
    public JdbcPagingItemReader<TransacaoDTO> reader() {
        JdbcPagingItemReader<TransacaoDTO> reader = new JdbcPagingItemReader<>();
        reader.setDataSource(dataSource);
        reader.setPageSize(100);

        MySqlPagingQueryProvider queryProvider = new MySqlPagingQueryProvider();
        queryProvider.setSelectClause("SELECT t.id, t.data_pedido, p.nome_produto, p.descricao_produto, p.valor_produto, " +
                "c_novo.nome AS nome_novo_dono, c_antigo.nome AS nome_antigo_dono");
        queryProvider.setFromClause("FROM transacoes t " +
                "INNER JOIN produtos p ON t.produto_id = p.id " +
                "INNER JOIN clientes c_novo ON c_novo.id = t.novo_dono_produto_id " +
                "INNER JOIN clientes c_antigo ON c_antigo.id = t.antigo_dono_produto_id");

        Map<String, Order> sortKeys = new HashMap<>();
        sortKeys.put("id", Order.ASCENDING);
        queryProvider.setSortKeys(sortKeys);

        reader.setQueryProvider(queryProvider);

        reader.setRowMapper((rs, rowNum) -> {
            TransacaoDTO transacaoDTO = new TransacaoDTO();
            transacaoDTO.setId(rs.getLong("id"));
            transacaoDTO.setDataPedido(rs.getTimestamp("data_pedido"));
            transacaoDTO.setNomeProduto(rs.getString("nome_produto"));
            transacaoDTO.setDescricaoProduto(rs.getString("descricao_produto"));
            transacaoDTO.setValorProduto(rs.getString("valor_produto"));
            transacaoDTO.setNomeNovoDono(rs.getString("nome_novo_dono"));
            transacaoDTO.setNomeAntigoDono(rs.getString("nome_antigo_dono"));
            return transacaoDTO;
        });

        return reader;
    }

    @Bean
    public ItemWriter<TransacaoDTO> writer() {
        return items -> {
            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Transações");

                // Criar cabeçalho no Excel
                Row headerRow = sheet.createRow(0);
                String[] columns = {"ID", "Data Pedido", "Nome Produto", "Descrição Produto", "Valor Produto", "Novo Dono", "Antigo Dono"};
                for (int i = 0; i < columns.length; i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(columns[i]);
                    // Estiliza o cabeçalho em negrito
                    CellStyle headerStyle = workbook.createCellStyle();
                    Font headerFont = workbook.createFont();
                    headerFont.setBold(true);
                    headerStyle.setFont(headerFont);
                    cell.setCellStyle(headerStyle);
                }

                // Preenche os dados do Excel com TransacaoDTO diretamente
                int rowNum = 1;
                for (TransacaoDTO transacao : items) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(transacao.getId());
                    row.createCell(1).setCellValue(transacao.getDataPedido().toString());
                    row.createCell(2).setCellValue(transacao.getNomeProduto());
                    row.createCell(3).setCellValue(transacao.getDescricaoProduto());
                    row.createCell(4).setCellValue(transacao.getValorProduto());
                    row.createCell(5).setCellValue(transacao.getNomeNovoDono());
                    row.createCell(6).setCellValue(transacao.getNomeAntigoDono());
                }

                // Ajustar colunas no Excel
                for (int i = 0; i < columns.length; i++) {
                    sheet.autoSizeColumn(i);
                }

                // Salvar no disco
                try (FileOutputStream fileOut = new FileOutputStream("C:\\Users\\gui\\Downloads\\Batch\\Batch\\src\\main\\java\\com\\batchBoaventuraMarketplace\\Batch\\excel\\transacoes.xlsx")) {
                    workbook.write(fileOut);
                }
            } catch (IOException e) {
                throw new RuntimeException("Erro ao salvar o Excel", e);
            }
        };
    }

    @Bean
    public Job job(JobRepository jobRepository,
                   PlatformTransactionManager transactionManager) {
        return new JobBuilder("exportTransacoesJob", jobRepository)
                .start(step(jobRepository, transactionManager))
                .build();
    }

    @Bean
    public Step step(JobRepository jobRepository,
                     PlatformTransactionManager transactionManager) {
        return new StepBuilder("step1", jobRepository)
                .<TransacaoDTO, TransacaoDTO>chunk(100, transactionManager)
                .reader(reader())
                .writer(writer())
                .build();
    }
}
