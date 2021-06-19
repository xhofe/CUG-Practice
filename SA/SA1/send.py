import boto3
import tkinter as tk
# Create SQS client
sqs = boto3.client('sqs')

queue_url = 'https://sqs.us-east-1.amazonaws.com/243671855485/test'

def send(s):
    # Send message to SQS queue
    response = sqs.send_message(
        QueueUrl=queue_url,
        DelaySeconds=10,
        MessageAttributes={
            'Title': {
                'DataType': 'String',
                'StringValue': 'The Whistler'
            },
            'Author': {
                'DataType': 'String',
                'StringValue': 'John Grisham'
            },
            'WeeksOn': {
                'DataType': 'Number',
                'StringValue': '3'
            }
        },
        MessageBody=s
    )
    return response['MessageId']

root=tk.Tk()
root.title('Send')
root.geometry('480x400')
text=tk.Text(root,width=200,)
text.pack()

entry=tk.Entry(root,width=200)
entry.pack()

def sendCall():
    s=entry.get()
    text.insert(tk.END,s+' ')
    messageId=send(s)
    text.insert('end',messageId+'\n')
    entry.delete(0,'end')

button=tk.Button(root,text='send',command=sendCall)
button.pack()

if __name__ == '__main__':
    root.mainloop()