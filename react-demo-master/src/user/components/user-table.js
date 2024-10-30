import React from "react";
import Table from "../../commons/tables/table";


const columns = [
    {
        Header: 'Name',
        accessor: 'name',
        Cell: ({ row, column }) => (
            <EditableCell
                value={row.original.name}
                onSave={(newValue) => handleSave(row.index, 'name', newValue)}
            />
        )
    },
    {
        Header: 'Role',
        accessor: 'role',
        Cell: ({ row, column }) => (
            <EditableCell
                value={row.original.role}
                onSave={(newValue) => handleSave(row.index, 'role', newValue)}
            />
        )
    }
];

const filters = [
    {
        accessor: 'name',
    }
];

class UserTable extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            tableData: this.props.tableData
        };
    }


    handleSave = async (rowIndex, field, newValue) => {
        const updatedData = [...this.state.tableData];
        updatedData[rowIndex][field] = newValue;
        // Se face un apel API pentru a salva modificarile in baza de date

        const userId = updatedData[rowIndex].id; // ID unic pentru utilizatori

        try {
            const response = await fetch(`http://tusa-url/api/users/${userId}`, {
                method: 'PUT', // Sau 'POST'
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    [field]: newValue // Trimite doar a campului care a fost actualizat
                }),
            });

            if (!response.ok) {
                throw new Error('Failed to update user');
            }

            // Se actualizeaza starea cu noile date
            this.setState({tableData: updatedData});
        } catch (error) {
            console.error('Error updating user:', error);
        }
    };


    render() {
        return (
            <Table
                data={this.state.tableData}
                columns={columns}
                search={filters}
                pageSize={5}
            />
        )
    }
}

// Componenta pentru celulele editabile
class EditableCell extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            isEditing: false,
            value: this.props.value,
        };
    }

    handleChange = (event) => {
        this.setState({value: event.target.value});
    };

    handleBlur = () => {
        this.setState({isEditing: false});
        this.props.onSave(this.state.value);
    };

    render() {
        if (this.state.isEditing) {
            return (
                <input
                    type="text"
                    value={this.state.value}
                    onChange={this.handleChange}
                    onBlur={this.handleBlur}
                    autoFocus
                />
            );
        }

        return (
            <span onClick={() => this.setState({ isEditing: true })}>
                {this.props.value}
            </span>
        );
    }

}

export default UserTable;
