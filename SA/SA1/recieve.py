import boto3
import tkinter as tk
import time
import _thread

# Create SQS client
sqs = boto3.client('sqs')

queue_url = 'https://sqs.us-east-1.amazonaws.com/243671855485/test'

def receive():
    # Receive message from SQS queue
    response = sqs.receive_message(
        QueueUrl=queue_url,
        AttributeNames=[
            'SentTimestamp'
        ],
        MaxNumberOfMessages=1,
        MessageAttributeNames=[
            'All'
        ],
        VisibilityTimeout=0,
        WaitTimeSeconds=0
    )
    print(response)
    return response

def delete(receipt_handle):
    # Delete received message from queue
    sqs.delete_message(
        QueueUrl=queue_url,
        ReceiptHandle=receipt_handle
    )

root=tk.Tk()
root.title('Receive')
root.geometry('480x400')
text=tk.Text(root,width=200,)
text.pack()

def recieveThread():
    while True:
        time.sleep(0.1)
        response=receive()
        if 'Messages' not in response:
            continue
        else:
            messages = response['Messages']
            for message in messages:
                text.insert('end',message['Body']+'\t'+message['MessageId']+'\n')
                receipt_handle = message['ReceiptHandle']
                delete(receipt_handle)

if __name__ == '__main__':
    _thread.start_new_thread(recieveThread,())
    root.mainloop()